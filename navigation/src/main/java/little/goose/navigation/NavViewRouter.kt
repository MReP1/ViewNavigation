package little.goose.navigation

import android.os.Bundle
import android.view.View

class NavViewRouter internal constructor(
    val route: String,
    val cached: Boolean,
    val viewBuilder: ViewNavigatorController.(Bundle?, Boolean) -> View
)