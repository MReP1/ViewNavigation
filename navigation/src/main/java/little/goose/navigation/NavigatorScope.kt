package little.goose.navigation

import android.content.Context
import android.view.View
import androidx.viewbinding.ViewBinding

interface NavigatorScope {

    val context: Context

    val navigator: ViewNavigator

    fun <T : View> navView(
        route: String,
        cached: Boolean = true,
        update: (ViewNavigatorController.(T, ViewStackEntry) -> Unit)? = null,
        onDetach: ((T, ViewStackEntry) -> Unit)? = null,
        onPop: ((T, String?) -> Boolean)? = null,
        builder: ViewNavigatorController.(ViewStackEntry) -> T
    )

    fun <T : ViewBinding> navViewBinding(
        route: String,
        cached: Boolean = true,
        update: (ViewNavigatorController.(T, ViewStackEntry) -> Unit)? = null,
        onDetach: ((T, ViewStackEntry) -> Unit)? = null,
        onPop: ((T, String?) -> Boolean)? = null,
        builder: ViewNavigatorController.(ViewStackEntry) -> T
    )

    fun <C : ViewController<V>, V : View> navViewController(
        route: String,
        cached: Boolean = true,
        builder: ViewNavigatorController.(ViewStackEntry) -> C
    )

}