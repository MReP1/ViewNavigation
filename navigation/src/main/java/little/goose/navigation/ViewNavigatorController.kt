package little.goose.navigation

fun ViewNavigatorController(): ViewNavigatorController = ViewNavigatorController.newInstance()

sealed interface ViewNavigatorController {

    companion object {
        fun newInstance(): ViewNavigatorController = ViewNavigatorControllerImpl()
    }

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
     * @param paramsBuilder build params
     * @return
     */
    fun popTo(route: String, paramsBuilder: NavigateParams.() -> Unit = {}): Boolean

}

internal class ViewNavigatorControllerImpl : ViewNavigatorController {

    internal var onNavigateToCall: (
        (route: String, paramsBuilder: NavigateParams.() -> Unit) -> Boolean
    )? = null

    internal var onPopCall: (
        () -> Boolean
    )? = null

    internal var onPopToCall: (
        (String, paramsBuilder: NavigateParams.() -> Unit) -> Boolean
    )? = null

    override fun navigateTo(route: String, paramsBuilder: NavigateParams.() -> Unit): Boolean {
        return onNavigateToCall?.invoke(route, paramsBuilder) ?: false
    }

    override fun pop(): Boolean {
        return onPopCall?.invoke() ?: false
    }

    override fun popTo(route: String, paramsBuilder: NavigateParams.() -> Unit): Boolean {
        return onPopToCall?.invoke(route, paramsBuilder) ?: false
    }

}