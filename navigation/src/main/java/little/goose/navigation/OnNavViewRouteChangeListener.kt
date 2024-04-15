package little.goose.navigation

fun interface OnNavViewRouteChangeListener {
    fun onRouterChange(oldRoute: String?, newRoute: String)
}