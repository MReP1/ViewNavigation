package little.goose.navigation

import android.os.Bundle
import android.view.View

class NavViewRouter internal constructor(
    val route: String,
    val cached: Boolean,
    internal val getArgs: () -> Bundle?,
    internal val setArgs: (Bundle?) -> Unit,
    val viewBuilder: ViewNavigatorController.(Bundle?, Boolean) -> View,
)