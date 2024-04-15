package little.goose.navigation

import android.view.View

interface ViewContainer<V: View> {

    val container: V

    val currentView: View?

    fun setView(view: View)

    fun animateChangeView(view: View, forward: Boolean)

}