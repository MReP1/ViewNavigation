package little.goose.navigation

import android.view.View

abstract class ViewController<V : View> {

    internal var cacheView: V? = null
    internal var cacheViewBinding: Any? = null

    abstract fun buildView(entry: ViewStackEntry): V

    abstract fun update(view: V, entry: ViewStackEntry)

    abstract fun onDetach(view: V, entry: ViewStackEntry)

    open fun onPop(view: V, targetRoute: String?): PopResult {
        return PopResult.DoNothing
    }

}