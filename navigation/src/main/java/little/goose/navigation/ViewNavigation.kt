package little.goose.navigation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.savedstate.SavedStateRegistry
import java.util.LinkedList

/**
 * View navigator
 *
 * create a ViewNavigator in Activity scope.
 *
 * @param name a unique name for the navigator.
 * @param navController create by [ViewNavigatorController], you can use it to navigate.
 * @param initRoute the initial route.
 * @param initArgs the initial args for the first screen.
 * @param defaultAnimations default animation params.
 * @param builder builder for the navigator.
 * @receiver
 * @return
 */
@MainThread
fun ComponentActivity.ViewNavigator(
    name: String,
    navController: ViewNavigatorController,
    initRoute: String,
    initArgs: Bundle? = null,
    defaultAnimations: AnimationParams = AnimationParams.DefaultAnimationParams,
    builder: NavigatorScope.() -> Unit
): ViewNavigator = ViewNavigatorImpl(
    this, savedStateRegistry, name,
    navController as ViewNavigatorControllerImpl,
    ViewNavigationContainerLayout(this).apply {
        animationParams = defaultAnimations
    },
    initRoute, initArgs, builder
)

/**
 * View navigator
 *
 * create a ViewNavigator in Fragment scope.
 *
 * @param name a unique name for the navigator.
 * @param navController create by [ViewNavigatorController], you can use it to navigate.
 * @param initRoute the initial route.
 * @param initArgs the initial args for the first screen.
 * @param defaultAnimations default animation params.
 * @param builder builder for the navigator.
 * @receiver
 * @return
 */
@MainThread
fun Fragment.ViewNavigator(
    name: String,
    navController: ViewNavigatorController,
    initRoute: String,
    initArgs: Bundle? = null,
    defaultAnimations: AnimationParams = AnimationParams.DefaultAnimationParams,
    builder: NavigatorScope.() -> Unit
): ViewNavigator = ViewNavigatorImpl(
    requireContext(), savedStateRegistry, name,
    navController as ViewNavigatorControllerImpl,
    ViewNavigationContainerLayout(requireContext()).apply {
        animationParams = defaultAnimations
    },
    initRoute, initArgs, builder
)

/**
 * View navigator
 *
 * create a ViewNavigator in Activity scope.
 *
 * @param name a unique name for the navigator.
 * @param navController create by [ViewNavigatorController], you can use it to navigate.
 * @param initRoute the initial route.
 * @param initArgs the initial args for the first screen.
 * @param container container for the navigator, you can use your own container or default [ViewNavigationContainerLayout].
 * @param builder builder for the navigator.
 * @receiver
 * @return
 */
@MainThread
fun ComponentActivity.ViewNavigator(
    name: String,
    navController: ViewNavigatorController,
    initRoute: String,
    initArgs: Bundle? = null,
    container: ViewContainer<out View>,
    builder: NavigatorScope.() -> Unit
): ViewNavigator = ViewNavigatorImpl(
    this, savedStateRegistry, name,
    navController as ViewNavigatorControllerImpl,
    container, initRoute, initArgs, builder
)

/**
 * View navigator
 *
 * create a ViewNavigator in Fragment scope.
 *
 * @param name a unique name for the navigator.
 * @param navController create by [ViewNavigatorController], you can use it to navigate.
 * @param initRoute the initial route.
 * @param initArgs the initial args for the first screen.
 * @param container container for the navigator, you can use your own container or default [ViewNavigationContainerLayout].
 * @param builder builder for the navigator.
 * @receiver
 * @return
 */
@MainThread
fun Fragment.ViewNavigator(
    name: String,
    navController: ViewNavigatorController,
    initRoute: String,
    initArgs: Bundle? = null,
    container: ViewContainer<out View>,
    builder: NavigatorScope.() -> Unit
): ViewNavigator = ViewNavigatorImpl(
    requireContext(), savedStateRegistry, name,
    navController as ViewNavigatorControllerImpl,
    container, initRoute, initArgs, builder
)

/**
 * View navigator
 *
 * @param context use current Activity please.
 * @param name a unique name for the navigator.
 * @param navController create by [ViewNavigatorController], you can use it to navigate.
 * @param initRoute the initial route.
 * @param initArgs the initial args for the first screen.
 * @param savedStateRegistry use to save state of route stack and args.
 * @param defaultAnimations default animation params.
 * @param builder builder for the navigator.
 * @receiver
 * @return
 */
@MainThread
fun ViewNavigator(
    context: Context,
    name: String,
    navController: ViewNavigatorController,
    initRoute: String,
    initArgs: Bundle? = null,
    savedStateRegistry: SavedStateRegistry? = null,
    defaultAnimations: AnimationParams = AnimationParams.DefaultAnimationParams,
    builder: NavigatorScope.() -> Unit
): ViewNavigator = ViewNavigatorImpl(
    context, savedStateRegistry, name,
    navController as ViewNavigatorControllerImpl,
    ViewNavigationContainerLayout(context).apply {
        animationParams = defaultAnimations
    }, initRoute, initArgs, builder
)

/**
 * View navigator
 *
 * @param context use current Activity please.
 * @param name a unique name for the navigator.
 * @param navController create by [ViewNavigatorController], you can use it to navigate.
 * @param initRoute the initial route.
 * @param initArgs the initial args for the first screen.
 * @param savedStateRegistry use to save state of route stack and args.
 * @param container container for the navigator, you can use your own container or default [ViewNavigationContainerLayout].
 * @param builder builder for the navigator.
 * @receiver
 * @return
 */
@MainThread
fun ViewNavigator(
    context: Context,
    name: String,
    navController: ViewNavigatorController,
    initRoute: String,
    initArgs: Bundle? = null,
    savedStateRegistry: SavedStateRegistry? = null,
    container: ViewContainer<out View>,
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
    var defaultOnPopListener: ((String?) -> PopResult)?

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

    override var defaultOnPopListener: ((String?) -> PopResult)? = null

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
        if (!force) {
            when (val popResult = checkCurrentViewIntercept(null)) {
                PopResult.DoNothing -> Unit
                PopResult.Intercept -> return true
                is PopResult.Redirect -> {
                    return popTo(popResult.targetRoute, true) {}
                }
            }
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
        if (!force) {
            when (val popResult = checkCurrentViewIntercept(targetRouter.route)) {
                PopResult.DoNothing -> Unit
                PopResult.Intercept -> return true
                is PopResult.Redirect -> {
                    return popTo(popResult.targetRoute, true) {}
                }
            }
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


    private fun checkCurrentViewIntercept(targetRoute: String?): PopResult {
        var popResult = navigatorRouterHolder.currentOnPop?.invoke(targetRoute)
            ?: PopResult.DoNothing
        if (popResult == PopResult.DoNothing) {
            popResult = defaultOnPopListener?.invoke(targetRoute) ?: PopResult.DoNothing
        }
        return popResult
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
    val restoreBundle = savedStateRegistry.consumeRestoredStateForKey(name)
    if (savedStateRegistry.getSavedStateProvider(name) != null) {
        // if a SavedStateRegistry contains multiple navigator with same name,
        // will lead to crash.
        // Such as more than one fragment contains same navigator.
        // TODO How to deal with it?
        throw IllegalArgumentException(
            "Do not create multi ViewNavigator with same name in a savedStateRegistry (UI Context)"
        )
    }
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
                    putBundle(key, args)
                }
            }
        }
    }
    val stackArray = restoreBundle?.getStringArray(KEY_STACK) ?: return null
    stackArray.forEachIndexed { index, route ->
        val router: NavViewRouter = getRouter(route)
        val stackIndex = stackArray.lastIndex - index
        val key = "${route}_$stackIndex"
        val restoredArgs = restoreBundle.getBundle(key)
        router.setArgs(stackIndex, restoredArgs)
        routerStack.add(router)
    }
    return routerStack.peek()
}