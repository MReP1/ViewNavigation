package little.goose.navigation

import android.os.Bundle
import android.view.View

class NavViewRouter internal constructor(
    val route: String,
    val cached: Boolean,
    internal val getArgs: (index: Int) -> Bundle?,
    internal val setArgs: (index: Int, Bundle?) -> Unit,
    internal val viewBuilder: ViewNavigatorController.(Int, Bundle?, Boolean) -> View,
)