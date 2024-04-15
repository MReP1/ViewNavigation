package little.goose.navigation

import android.os.Bundle

fun ViewNavigatorController(): ViewNavigatorController = ViewNavigatorControllerImpl()

sealed interface ViewNavigatorController {

    /**
     * Navigate to target route view.
     *
     * @param route target route
     * @param paramsBuilder build params
     * @return
     */
    fun navigateTo(route: String, paramsBuilder: NavigateParams.() -> Unit = {}): Boolean

    /**
     * Pop current view.
     *
     * @return
     */
    fun pop(): Boolean

    /**
     * Pop to target route contained in view stack.
     *
     * @param route target route
     * @param args arguments to build view or update view
     * @return
     */
    fun popTo(route: String, args: Bundle? = null): Boolean

}

internal class ViewNavigatorControllerImpl : ViewNavigatorController {

    internal var onNavigateToCall: (
        (route: String, paramsBuilder: NavigateParams.() -> Unit) -> Boolean
    )? = null

    internal var onPopCall: (
        () -> Boolean
    )? = null

    internal var onPopToCall: (
        (String, Bundle?) -> Boolean
    )? = null

    override fun navigateTo(route: String, paramsBuilder: NavigateParams.() -> Unit): Boolean {
        return onNavigateToCall?.invoke(route, paramsBuilder) ?: false
    }

    override fun pop(): Boolean {
        return onPopCall?.invoke() ?: false
    }

    override fun popTo(route: String, args: Bundle?): Boolean {
        return onPopToCall?.invoke(route, args) ?: false
    }

}