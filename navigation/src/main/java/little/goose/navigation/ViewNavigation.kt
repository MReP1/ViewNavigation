package little.goose.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.savedstate.SavedStateRegistry
import java.util.LinkedList

private const val TAG = "ViewNavigator"

@MainThread
fun ComponentActivity.ViewNavigator(
    navController: ViewNavigatorController,
    initRoute: String,
    name: String,
    container: ViewContainer<out View> = ViewNavigationContainerLayout(this),
    initArgs: Bundle? = null,
    builder: NavigatorScope.() -> Unit
): ViewNavigator = ViewNavigatorImpl(
    this, savedStateRegistry, name,
    navController as ViewNavigatorControllerImpl,
    container, initRoute, initArgs, builder
)

@MainThread
fun Fragment.ViewNavigator(
    navController: ViewNavigatorController,
    initRoute: String,
    name: String,
    container: ViewContainer<out View> = ViewNavigationContainerLayout(requireContext()),
    initArgs: Bundle? = null,
    builder: NavigatorScope.() -> Unit
): ViewNavigator = ViewNavigatorImpl(
    requireContext(), savedStateRegistry, name,
    navController as ViewNavigatorControllerImpl,
    container, initRoute, initArgs, builder
)

@MainThread
fun ViewNavigator(
    context: Context,
    name: String,
    navController: ViewNavigatorController,
    initRoute: String,
    savedStateRegistry: SavedStateRegistry? = null,
    container: ViewContainer<out View> = ViewNavigationContainerLayout(context),
    initArgs: Bundle? = null,
    builder: NavigatorScope.() -> Unit
): ViewNavigator = ViewNavigatorImpl(
    context, savedStateRegistry, name,
    navController as ViewNavigatorControllerImpl,
    container, initRoute, initArgs, builder
)

sealed interface ViewNavigator {

    /**
     * Container View
     */
    val containerView: View

    /**
     * View stack size included foreground view
     */
    val viewStackSize: Int

    /**
     * Current route with foreground view
     */
    val currentRoute: String?

    /**
     * Foreground view
     */
    val currentView: View?

    /**
     * Navigator controller
     */
    val navigatorController: ViewNavigatorController

    /**
     * Default on pop listener, call when all router's onPop function return false.
     * when it called returning false, the real pop logic will be run.
     * null if there only one screen.
     */
    var defaultOnPopListener: ((String?) -> Boolean)?

    /**
     * Add route change listener, when current foreground route change,
     * [OnNavViewRouteChangeListener.onRouterChange] view be invoked
     *
     * @param listener
     */
    fun addRouteChangeListener(listener: OnNavViewRouteChangeListener)

    /**
     * Remote route change listener
     *
     * @param listener
     */
    fun remoteRouteChangeListener(listener: OnNavViewRouteChangeListener)

    /**
     * Add on pop out listener,
     * listener will be invoked when pop call in the meantime the view stack is empty
     *
     * @param listener
     * @receiver if intercept pop event.
     */
    fun addOnPopOutListener(listener: () -> Boolean)

    /**
     * Remove on pop out listener
     *
     * @param listener
     * @receiver if intercept pop event.
     */
    fun removeOnPopOutListener(listener: () -> Boolean)

}

@MainThread
private class ViewNavigatorImpl(
    context: Context,
    savedStateRegistry: SavedStateRegistry?,
    name: String,
    override val navigatorController: ViewNavigatorControllerImpl,
    private val viewContainer: ViewContainer<out View>,
    initRoute: String,
    initArgs: Bundle? = null,
    builder: NavigatorScope.() -> Unit
) : ViewNavigator {

    override val containerView: View get() = viewContainer.container

    private val navigatorRouterHolder = NavigatorRouterHolder(context, this)

    private val routerStack: LinkedList<NavViewRouter> = LinkedList()

    private val currentRouter: NavViewRouter? get() = routerStack.peek()

    private val rougeChangeListeners: LinkedHashSet<OnNavViewRouteChangeListener> = LinkedHashSet()

    private val onPopOutListeners: LinkedHashSet<(() -> Boolean)> = LinkedHashSet()

    override val viewStackSize: Int get() = routerStack.size

    override val currentRoute: String? get() = currentRouter?.route

    override val currentView: View? get() = viewContainer.currentView

    override var defaultOnPopListener: ((String?) -> Boolean)? = null

    init {
        containerView.tag = name
        navigatorController.onPopCall = ::pop
        navigatorController.onPopToCall = ::popTo
        navigatorController.onNavigateToCall = { r, b -> navigateTo(r, animate = true, b) }
        builder(navigatorRouterHolder)
        val restoreRoute = savedStateRegistry?.let {
            initSavedStateAndGetRestoreRouter(
                name, savedStateRegistry, routerStack,
                navigatorRouterHolder::getRouter
            )
        }
        if (restoreRoute == null) {
            navigateTo(initRoute, animate = false) { args = initArgs }
        } else {
            viewContainer.setView(
                restoreRoute.viewBuilder(
                    navigatorController, routerStack.lastIndex, null, false
                )
            )
        }
    }

    private fun navigateTo(
        route: String,
        animate: Boolean,
        paramsBuilder: NavigateParams.() -> Unit
    ): Boolean {
        val current = currentRouter
        if (current?.route == route && current.cached) {
            return false
        }
        val router = navigatorRouterHolder.getRouter(route)
        val params = NavigateParams(paramsBuilder)
        val view = router.viewBuilder(
            navigatorController, routerStack.lastIndex + 1, params.args, true
        )
        if (!animate) {
            viewContainer.setView(view = view)
        } else {
            viewContainer.animateChangeView(view = view, forward = true)
        }
        routerStack.push(router)
        for (listener in rougeChangeListeners) {
            listener.onRouterChange(current?.route, router.route)
        }
        return true
    }

    private fun pop(force: Boolean): Boolean {
        val targetRouter = routerStack.getOrNull(1)
        if (!force && checkCurrentViewIntercept(null)) {
            return true
        }
        if (routerStack.size == 1 || targetRouter == null) {
            return onPopOutListeners.any { it() }
        }
        val current = currentRouter
        routerStack.pop()
        val targetIndex = routerStack.lastIndex
        val view = targetRouter.viewBuilder(navigatorController, targetIndex, null, false)
        viewContainer.animateChangeView(view, forward = false)
        for (listener in rougeChangeListeners) {
            listener.onRouterChange(current?.route, targetRouter.route)
        }
        return true
    }

    private fun popTo(
        route: String,
        force: Boolean,
        paramsBuilder: NavigateParams.() -> Unit
    ): Boolean {
        val current = currentRouter
        if (current?.route == route && current.cached) {
            return false
        }
        val targetRouter = navigatorRouterHolder.getRouter(route)
        if (routerStack.indexOf(targetRouter) == -1) {
            return false
        }
        if (!force && checkCurrentViewIntercept(targetRouter.route)) {
            return true
        }
        var router: NavViewRouter?
        var routerIndex: Int
        do {
            routerStack.pop()
            router = routerStack.peek()
            routerIndex = routerStack.lastIndex
        } while (router != targetRouter && routerStack.size > 1)
        router?.let {
            val params = NavigateParams(paramsBuilder)
            val view = it.viewBuilder(
                navigatorController, routerIndex, params.args, params.args != null
            )
            viewContainer.animateChangeView(view, forward = false)
            for (listener in rougeChangeListeners) {
                listener.onRouterChange(current?.route, router.route)
            }
            return true
        }
        return false
    }


    private fun checkCurrentViewIntercept(targetRoute: String?): Boolean {
        var isIntercept = navigatorRouterHolder.currentOnPop?.invoke(targetRoute) ?: false
        if (!isIntercept) {
            isIntercept = defaultOnPopListener?.invoke(targetRoute) ?: false
        }
        return isIntercept
    }

    override fun addRouteChangeListener(listener: OnNavViewRouteChangeListener) {
        rougeChangeListeners.add(listener)
    }

    override fun remoteRouteChangeListener(listener: OnNavViewRouteChangeListener) {
        rougeChangeListeners.remove(listener)
    }

    override fun addOnPopOutListener(listener: () -> Boolean) {
        onPopOutListeners.add(listener)
    }

    override fun removeOnPopOutListener(listener: () -> Boolean) {
        onPopOutListeners.remove(listener)
    }

}

private const val KEY_STACK = "stack"

private inline fun initSavedStateAndGetRestoreRouter(
    name: String,
    savedStateRegistry: SavedStateRegistry,
    routerStack: LinkedList<NavViewRouter>,
    getRouter: (String) -> NavViewRouter
): NavViewRouter? {
    savedStateRegistry.registerSavedStateProvider(name) {
        Bundle().apply {
            putStringArray(KEY_STACK, routerStack.map(NavViewRouter::route).toTypedArray())
            val stringArrays = arrayOfNulls<String>(routerStack.size)
            routerStack.forEachIndexed { index: Int, router: NavViewRouter ->
                stringArrays[index] = router.route
                val stackIndex = routerStack.lastIndex - index
                val args = router.getArgs(stackIndex)
                if (args != null) {
                    val key = "${router.route}_$stackIndex"
                    Log.d(TAG, "save bundle key: $key, save args: $args")
                    putBundle(key, args)
                }
            }
        }
    }
    val restoreBundle = savedStateRegistry.consumeRestoredStateForKey(name)
    val stackArray = restoreBundle?.getStringArray(KEY_STACK) ?: return null
    stackArray.forEachIndexed { index, route ->
        val router: NavViewRouter = getRouter(route)
        val stackIndex = stackArray.lastIndex - index
        val key = "${route}_$stackIndex"
        val restoredArgs = restoreBundle.getBundle(key)
        Log.d(TAG, "get bundle key: $key, restore args: $restoredArgs")
        router.setArgs(stackIndex, restoredArgs)
        routerStack.add(router)
    }
    return routerStack.peek()
}