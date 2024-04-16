package little.goose.navigation

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

internal class NavigatorRouterHolder(override val context: Context) : NavigatorScope {

    private val routerMap = mutableMapOf<String, NavViewRouter>()

    internal var currentOnPop: ((String?) -> Boolean)? = null

    internal fun getRouter(route: String): NavViewRouter {
        return requireNotNull(routerMap[route]) { "You have not set route $route yet." }
    }

    override fun <T : View> navView(
        route: String,
        cached: Boolean,
        onAttach: (ViewNavigatorController.(T, ViewStackEntry) -> Unit)?,
        onDetach: ((T, ViewStackEntry) -> Unit)?,
        onPop: ((T, String?) -> Boolean)?,
        builder: ViewNavigatorController.(ViewStackEntry) -> T
    ) {
        var cachedView: T? = null
        val entry = ViewStackEntry(null, Job())
        routerMap[route] = NavViewRouter(
            route = route,
            cached = cached,
            getArgs = entry::args::get,
            setArgs = entry::args::set,
            viewBuilder = { bundle, updateBundle ->
                if (updateBundle) {
                    entry.args = bundle
                }
                if (cached && cachedView != null) {
                    cachedView!!.also { (it.parent as? ViewGroup)?.removeView(it) }
                } else {
                    builder(entry).also { view ->
                        if (cached) {
                            cachedView = view
                        }
                    }
                }.also { view ->
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