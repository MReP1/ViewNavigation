package little.goose.navigation

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

internal class NavigatorRouterHolder(
    override val context: Context,
    override val navigator: ViewNavigator
) : NavigatorScope {

    companion object {
        private const val TAG = "NavigatorRouterHolder"
    }

    private val routerMap = mutableMapOf<String, NavViewRouter>()

    internal var currentOnPop: ((String?) -> Boolean)? = null

    internal fun getRouter(route: String): NavViewRouter {
        return requireNotNull(routerMap[route]) { "You have not set route $route yet." }
    }

    private val entryMap = mutableMapOf<String, ViewStackEntry>()
    private val cachedViewMap = mutableMapOf<String, View>()

    private fun getKey(route: String, index: Int) = "${route}_${index}"

    private fun getEntry(route: String, index: Int): ViewStackEntry {
        return entryMap[getKey(route, index)] ?: ViewStackEntry(null, Job()).also {
            entryMap[getKey(route, index)] = it
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : View> navView(
        route: String,
        cached: Boolean,
        onAttach: (ViewNavigatorController.(T, ViewStackEntry) -> Unit)?,
        onDetach: ((T, ViewStackEntry) -> Unit)?,
        onPop: ((T, String?) -> Boolean)?,
        builder: ViewNavigatorController.(ViewStackEntry) -> T
    ) {
        routerMap[route] = NavViewRouter(
            route = route,
            cached = cached,
            getArgs = { index -> getEntry(route, index).args },
            setArgs = { index, bundle -> getEntry(route, index).args = bundle },
            viewBuilder = { index, bundle, updateBundle ->
                val entry = getEntry(route, index)
                if (updateBundle) {
                    entry.args = bundle
                }
                Log.d(TAG, "navView: getKey ${getKey(route, index)} ${entry.args}")
                val cachedView = cachedViewMap[route] as? T
                (if (cached && cachedView != null) {
                    cachedView.also { (it.parent as? ViewGroup)?.removeView(it) }
                } else {
                    builder(entry).also { view ->
                        if (cached) {
                            cachedViewMap[route] = view
                        }
                    }
                }).also { view ->
                    view.doOnAttach {
                        if (!entry.job.isActive) {
                            entry.job = Job()
                        }
                        currentOnPop = { onPop?.invoke(view, it) ?: false }
                        onAttach?.invoke(this@NavViewRouter, view, entry)
                    }
                    view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                        override fun onViewAttachedToWindow(v: View) {
                        }

                        @Suppress("UNCHECKED_CAST")
                        override fun onViewDetachedFromWindow(v: View) {
                            entry.job.cancel()
                            ((v as? T) ?: cachedView)?.let { onDetach?.invoke(it, entry) }
                            v.removeOnAttachStateChangeListener(this)
                        }
                    })
                }
            }
        )
    }

    override fun <T : ViewBinding> navViewBinding(
        route: String,
        onAttach: (ViewNavigatorController.(T, ViewStackEntry) -> Unit)?,
        onDetach: ((T, ViewStackEntry) -> Unit)?,
        onPop: ((T, String?) -> Boolean)?,
        builder: ViewNavigatorController.(ViewStackEntry) -> T
    ) {
        var binding: T? = null
        navView(
            route, cached = true,
            onAttach = { _, entry -> binding?.let { b -> onAttach?.invoke(this, b, entry) } },
            onDetach = { _, entry -> onDetach?.invoke(binding!!, entry) },
            onPop = { _, targetRoute -> onPop?.invoke(binding!!, targetRoute) ?: false },
            builder = { entry -> builder(entry).also { binding = it }.root }
        )
    }

    override fun <C : ViewController<V>, V : View> navViewController(
        route: String,
        builder: ViewNavigatorController.(ViewStackEntry) -> C
    ) {
        var navController: C? = null
        navView(
            route, cached = true,
            onAttach = { view, entry -> navController?.update(view, entry) },
            onDetach = { view, entry -> navController?.onDetach(view, entry) },
            onPop = { view, targetRoute -> navController?.onPop(view, targetRoute) ?: false },
            builder = { entry -> builder(entry).also { navController = it }.buildView(entry) }
        )
    }
}