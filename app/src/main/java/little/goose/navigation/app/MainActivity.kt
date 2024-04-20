package little.goose.navigation.app

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import little.goose.navigation.ViewNavigator
import little.goose.navigation.ViewNavigatorController
import little.goose.navigation.app.databinding.ScreenOneBinding
import little.goose.navigation.app.component.CoroutineSampleViewController
import little.goose.navigation.app.util.addButton
import little.goose.navigation.app.util.addSpace
import little.goose.navigation.app.util.addText
import little.goose.navigation.app.util.linearLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val navigator = mainNavigator()
        setContentView(navigator.containerView)
        ViewCompat.setOnApplyWindowInsetsListener(navigator.containerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navigator.addOnPopOutListener {
            finish()
            true
        }
    }
}

const val MAIN_NAVIGATOR = "main_navigator"

object MainRoute {
    const val SCREEN_ONE = "SCREEN_ONE"
    const val SCREEN_TWO = "SCREEN_TWO"
    const val SCREEN_COROUTINE_SAMPLE = "SCREEN_COROUTINE_SAMPLE"
}

object MainTag {
    object ScreenTwo {
        const val TITLE = "screen_two_title"
        const val BUTTON_NAVIGATE_TO_COROUTINE = "screen_two_button_navigate_to_coroutine"
        const val CONTAINER = "screen_two_container"
    }

    object ScreenThree {
        const val BUTTON_BACK_TO_SCREEN_ONE = "screen_three_back_to_screen_one"
    }
}

object MainKey {
    const val SCREEN_ONE_TITLE = "INIT_TITLE"
    const val SCREEN_TWO_TITLE = "SCREEN_TWO_TITLE"
}

object MainValue {
    const val SCREEN_TWO_TITLE_FROM_ONE = "Screen two title from params"
    const val SCREEN_ONE_TITLE_FROM_THREE = "Title from other screen"
}

fun MainActivity.mainNavigator(): ViewNavigator {
    val navController = ViewNavigatorController()

    onBackPressedDispatcher.addCallback(this) {
        // Intercept back pressed event from MainActivity.
        navController.pop()
    }

    return ViewNavigator(
        navController = navController,
        initRoute = MainRoute.SCREEN_ONE,
        name = MAIN_NAVIGATOR
    ) {

        // Sample of Xml and ViewBinding
        navViewBinding(
            route = MainRoute.SCREEN_ONE,
            onAttach = { binding, entry ->
                binding.tvTitle.text = entry.args?.getString(MainKey.SCREEN_ONE_TITLE) ?: "Untitled"
            }
        ) {
            ScreenOneBinding.inflate(layoutInflater).apply {
                btNavToScreenTwo.setOnClickListener {
                    navigateTo(MainRoute.SCREEN_TWO) {
                        args =
                            bundleOf(MainKey.SCREEN_TWO_TITLE to MainValue.SCREEN_TWO_TITLE_FROM_ONE)
                    }
                }
            }
        }

        // Sample of a simple view without and logic.
        navView(MainRoute.SCREEN_TWO) { entry ->
            linearLayout(context, tag = MainTag.ScreenTwo.CONTAINER) {
                addSpace(1F)
                addText(
                    text = entry.args?.getString(MainKey.SCREEN_TWO_TITLE) ?: "Unknown",
                    tag = MainTag.ScreenTwo.TITLE
                )
                addButton(text = "Pop", onClick = ::pop)
                addButton(
                    text = "Navigate to Coroutine Sample Screen",
                    tag = MainTag.ScreenTwo.BUTTON_NAVIGATE_TO_COROUTINE,
                    onClick = { navigateTo(MainRoute.SCREEN_COROUTINE_SAMPLE) }
                )
                addSpace(1F)
            }
        }

        // Sample of a ViewController with coroutine and ViewModel logic.
        navViewController(MainRoute.SCREEN_COROUTINE_SAMPLE) {
            CoroutineSampleViewController(
                activity = this@mainNavigator,
                popToOneWithParams = {
                    popTo(MainRoute.SCREEN_ONE) {
                        args = bundleOf(
                            MainKey.SCREEN_ONE_TITLE to MainValue.SCREEN_ONE_TITLE_FROM_THREE
                        )
                    }
                }
            )
        }
    }
}