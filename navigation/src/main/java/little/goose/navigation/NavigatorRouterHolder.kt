package little.goose.navigation

import android.content.Context
import android.view.View
import androidx.core.view.doOnAttach
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job

internal class NavigatorRouterHolder(
    override val context: Context,
    override val navigator: ViewNavigator
) : NavigatorScope {

    private val routerMap = mutableMapOf<String, NavViewRouter>()

    internal var currentOnPop: ((String?) -> PopResult)? = null

    internal fun getRouter(route: String): NavViewRouter {
        return requireNotNull(routerMap[route]) { "You have not set route $route yet." }
    }

    private val entryMap = mutableMapOf<String, ViewStackEntry>()

    private val viewControllerMap = mutableMapOf<String, ViewController<View>>()

    private fun getKey(route: String, index: Int) = "${route}_${index}"

    private fun getEntry(route: String, index: Int): ViewStackEntry {
        return entryMap.getOrPut(getKey(route, index)) {
            ViewStackEntry(null, Job())
        }
    }

    override fun <T : View> navView(
        route: String,
        cached: Boolean,
        update: (ViewNavigatorController.(T, ViewStackEntry) -> Unit)?,
        onDetach: ((T, ViewStackEntry) -> Unit)?,
        onPop: ((T, String?) -> PopResult)?,
        builder: ViewNavigatorController.(ViewStackEntry) -> T
    ) {
        navViewController(
            route = route,
            cached = cached,
            builder = {
                object : ViewController<T>() {
                    override fun buildView(entry: ViewStackEntry): T {
                        return builder(this@navViewController, entry)
                    }

                    override fun onDetach(view: T, entry: ViewStackEntry) {
                        onDetach?.invoke(view, entry)
                    }

                    override fun update(view: T, entry: ViewStackEntry) {
                        update?.invoke(this@navViewController, view, entry)
                    }

                    override fun onPop(view: T, targetRoute: String?): PopResult {
                        return onPop?.invoke(view, targetRoute) ?: PopResult.DoNothing
                    }
                }
            }
        )
    }

    override fun <T : ViewBinding> navViewBinding(
        route: String,
        cached: Boolean,
        update: (ViewNavigatorController.(T, ViewStackEntry) -> Unit)?,
        onDetach: ((T, ViewStackEntry) -> Unit)?,
        onPop: ((T, String?) -> PopResult)?,
        builder: ViewNavigatorController.(ViewStackEntry) -> T
    ) {
        navViewController(
            route = route,
            cached = cached,
            builder = {
                object : ViewController<View>() {
                    override fun buildView(entry: ViewStackEntry): View {
                        val binding: T = builder(entry)
                        this.cacheViewBinding = binding
                        return binding.root
                    }

                    override fun onDetach(view: View, entry: ViewStackEntry) {
                        (this.cacheViewBinding as? T)?.let { viewBinding ->
                            onDetach?.invoke(viewBinding, entry)
                        }
                    }

                    override fun update(view: View, entry: ViewStackEntry) {
                        (this.cacheViewBinding as? T)?.let { viewBinding ->
                            update?.invoke(this@navViewController, viewBinding, entry)
                        }
                    }

                    override fun onPop(view: View, targetRoute: String?): PopResult {
                        return (this.cacheViewBinding as? T)?.let { viewBinding ->
                            onPop?.invoke(viewBinding, targetRoute) ?: PopResult.DoNothing
                        } ?: PopResult.DoNothing
                    }
                }
            }
        )
    }

    override fun <C : ViewController<V>, V : View> navViewController(
        route: String,
        cached: Boolean,
        builder: ViewNavigatorController.(ViewStackEntry) -> C
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
                var viewController: C?
                val view: V = if (cached) {
                    val key = getKey(route, index)
                    viewController = viewControllerMap[key] as? C
                    if (viewController == null) {
                        viewController = builder(entry)
                        viewControllerMap[key] = viewController as ViewController<View>
                    }
                    var view = viewController.cacheView
                    if (view == null) {
                        view = viewController.buildView(entry)
                        viewController.cacheView = view
                    }
                    view
                } else {
                    viewController = builder(entry)
                    viewController.buildView(entry)
                }
                view.doOnAttach {
                    if (!entry.job.isActive) {
                        entry.job = Job()
                    }
                    currentOnPop = { targetRoute ->
                        viewController.onPop(view, targetRoute)
                    }
                    viewController.update(view, entry)
                }
                view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                    override fun onViewAttachedToWindow(v: View) {
                    }

                    override fun onViewDetachedFromWindow(v: View) {
                        entry.job.cancel()
                        viewController.onDetach(view, entry)
                        v.removeOnAttachStateChangeListener(this)
                    }
                })
                view
            }
        )
    }

}