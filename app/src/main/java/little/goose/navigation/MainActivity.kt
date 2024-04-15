package little.goose.navigation

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import little.goose.navigation.component.CoroutineSampleViewController
import little.goose.navigation.databinding.ScreenOneBinding
import little.goose.navigation.util.addButton
import little.goose.navigation.util.addSpace
import little.goose.navigation.util.addText
import little.goose.navigation.util.linearLayout

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

private object Route {
    const val SCREEN_ONE = "SCREEN_ONE"
    const val SCREEN_TWO = "SCREEN_TWO"
    const val SCREEN_COROUTINE_SAMPLE = "SCREEN_COROUTINE_SAMPLE"
}

private object Key {
    const val SCREEN_ONE_TITLE = "INIT_TITLE"
    const val SCREEN_TWO_TITLE = "SCREEN_TWO_TITLE"
}

fun MainActivity.mainNavigator(): ViewNavigator {
    val navController = ViewNavigatorController()

    onBackPressedDispatcher.addCallback(this) {
        // Intercept back pressed event from MainActivity.
        navController.pop()
    }

    return ViewNavigator(this, navController, Route.SCREEN_ONE) {

        // Sample of Xml and ViewBinding
        navViewBinding(Route.SCREEN_ONE, onAttach = { binding, entry ->
            binding.tvTitle.text = entry.args?.getString(Key.SCREEN_ONE_TITLE) ?: "Untitled"
        }) {
            ScreenOneBinding.inflate(layoutInflater).apply {
                button.setOnClickListener {
                    navigateTo(Route.SCREEN_TWO) {
                        args = bundleOf(Key.SCREEN_TWO_TITLE to "Screen two title from params")
                    }
                }
            }
        }

        // Sample of a simple view without and logic.
        navView(Route.SCREEN_TWO) { entry ->
            linearLayout(context) {
                addSpace(1F)
                addText(text = entry.args?.getString(Key.SCREEN_TWO_TITLE) ?: "Unknown")
                addButton(text = "Pop", onClick = ::pop)
                addButton(
                    text = "Navigate to Coroutine Sample Screen",
                    onClick = { navigateTo(Route.SCREEN_COROUTINE_SAMPLE) }
                )
                addSpace(1F)
            }
        }

        // Sample of a ViewController with coroutine and ViewModel logic.
        navViewController(Route.SCREEN_COROUTINE_SAMPLE) {
            CoroutineSampleViewController(this@mainNavigator, popToOneWithParams = {
                popTo(
                    Route.SCREEN_ONE,
                    args = bundleOf(Key.SCREEN_ONE_TITLE to "Title from other screen")
                )
            })
        }
    }
}