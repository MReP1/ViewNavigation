package little.goose.navigation.app

import android.os.Bundle
import android.view.Gravity
import androidx.activity.ComponentActivity
import little.goose.design.util.addButton
import little.goose.design.util.linearLayout
import little.goose.navigation.PopResult
import little.goose.navigation.ViewNavigator
import little.goose.navigation.ViewNavigatorController

object PopRedirectTag {
    const val NAVIGATE_TO_TWO = "navigate_to_two"
    const val NAVIGATE_TO_THREE = "navigate_to_three"
    const val POP = "pop"
}

class PopRedirectActivity : ComponentActivity() {

    companion object {
        const val TAG = "PopRedirectActivity"
    }

    private object Route {
        const val ONE = "screen_one"
        const val TWO = "screen_two"
        const val THREE = "screen_three"
    }

    private val navController by lazy {
        ViewNavigatorController()
    }

    private val navigator by lazy {
        ViewNavigator(
            name = TAG,
            navController = navController,
            initRoute = Route.ONE,
        ) {
            navView(Route.ONE) {
                linearLayout(context, gravity = Gravity.CENTER) {
                    addButton(
                        text = "Navigate to screen two",
                        onClick = { navigateTo(Route.TWO) },
                        tag = PopRedirectTag.NAVIGATE_TO_TWO
                    )
                }
            }

            navView(Route.TWO) {
                linearLayout(context, gravity = Gravity.CENTER) {
                    addButton(
                        text = "Navigate to screen three",
                        onClick = { navigateTo(Route.THREE) },
                        tag = PopRedirectTag.NAVIGATE_TO_THREE
                    )
                }
            }

            navView(Route.THREE, onPop = { _, _ ->
                PopResult.Redirect(Route.ONE)
            }) {
                linearLayout(context, gravity = Gravity.CENTER) {
                    addButton(
                        text = "Pop and redirect to screen one",
                        onClick = ::pop,
                        tag = PopRedirectTag.POP
                    )
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(navigator.containerView)
    }

}