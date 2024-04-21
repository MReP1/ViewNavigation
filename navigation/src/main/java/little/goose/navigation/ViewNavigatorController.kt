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
     * @param force flag that ignore sub navigation's onPop
     * @return
     */
    fun pop(
        force: Boolean = false
    ): Boolean

    /**
     * Pop to target route contained in view stack.
     *
     * @param route target route
     * @param force flag that ignore sub navigation's onPop
     * @param paramsBuilder build params
     * @return
     */
    fun popTo(
        route: String,
        force: Boolean = false,
        paramsBuilder: NavigateParams.() -> Unit = {}
    ): Boolean

}

internal class ViewNavigatorControllerImpl : ViewNavigatorController {

    internal var onNavigateToCall: (
        (route: String, paramsBuilder: NavigateParams.() -> Unit) -> Boolean
    )? = null

    internal var onPopCall: (
        (Boolean) -> Boolean
    )? = null

    internal var onPopToCall: (
        (String, Boolean, paramsBuilder: NavigateParams.() -> Unit) -> Boolean
    )? = null

    override fun navigateTo(route: String, paramsBuilder: NavigateParams.() -> Unit): Boolean {
        return onNavigateToCall?.invoke(route, paramsBuilder) ?: false
    }

    override fun pop(force: Boolean): Boolean {
        return onPopCall?.invoke(force) ?: false
    }

    override fun popTo(
        route: String,
        force: Boolean,
        paramsBuilder: NavigateParams.() -> Unit
    ): Boolean {
        return onPopToCall?.invoke(route, force, paramsBuilder) ?: false
    }

}