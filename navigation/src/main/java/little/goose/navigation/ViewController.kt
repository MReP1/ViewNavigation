package little.goose.navigation

import android.view.View

abstract class ViewController<V : View> {

    abstract fun buildView(entry: ViewStackEntry): V

    abstract fun update(view: V, entry: ViewStackEntry)

    abstract fun onDetach(view: V, entry: ViewStackEntry)

    open fun onPop(view: V, targetRoute: String?): Boolean {
        return false
    }

}