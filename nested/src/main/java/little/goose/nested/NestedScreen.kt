package little.goose.nested

import little.goose.navigation.NavigatorScope

const val ROUTE_NESTED_SCREEN = "route_nested_screen"

fun NavigatorScope.navNestedScreen(
    popToScreenOne: () -> Unit
) {
    navViewController(route = ROUTE_NESTED_SCREEN) {
        NestedController(
            context = context,
            pop = { pop(force = true) },
            popToScreenOne = popToScreenOne
        )
    }
}