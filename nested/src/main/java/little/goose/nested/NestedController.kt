package little.goose.nested

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import com.google.android.material.card.MaterialCardView
import little.goose.design.util.addButton
import little.goose.design.util.addSpace
import little.goose.design.util.addText
import little.goose.design.util.dp
import little.goose.design.util.frameLayout
import little.goose.design.util.linearLayout
import little.goose.navigation.ViewController
import little.goose.navigation.ViewNavigator
import little.goose.navigation.ViewNavigatorController
import little.goose.navigation.ViewStackEntry

object NestedRoute {
    const val REPEAT_VIEW = "nested_preview_view"
}

object NestedTag {
    const val BUTTON_ADD_ONE_REPEAT_VIEW = "nested_button_add_one_repeat_view"
    const val BUTTON_POP_TO_SCREEN_ONE = "nested_button_pop_to_screen_one"
    const val BUTTON_POP = "nested_button_pop"
}

class NestedController(
    private val context: Context,
    private val pop: () -> Boolean,
    private val popToScreenOne: () -> Unit
) : ViewController<View>() {

    private companion object Key {
        const val TITLE = "title"
    }

    private var navController: ViewNavigatorController? = null

    override fun buildView(entry: ViewStackEntry): View {
        return linearLayout(context = context) {
            addText(text = "Nested screen", textSize = 26F) {
                setMargins(32.dp, 16.dp, 32.dp, 16.dp)
            }

            val navController = ViewNavigatorController()
            this@NestedController.navController = navController
            val navigator = createNavigator(navController)
            navigator.addOnPopOutListener(pop)

            val tvScreenSize = addText(
                text = "Screen size ${navigator.viewStackSize}",
                textSize = 16F
            )

            navigator.addRouteChangeListener { _, _ ->
                tvScreenSize.text = "Screen size ${navigator.viewStackSize}"
            }

            addSpace(8.dp)

            addButton(
                text = "pop sub navigator",
                onClick = navController::pop,
                tag = NestedTag.BUTTON_POP
            )

            addButton(
                text = "add one RepeatView",
                onClick = {
                    navController.navigateTo(NestedRoute.REPEAT_VIEW) {
                        args = Bundle().apply {
                            putString(
                                TITLE,
                                "RepeatView ${navigator.viewStackSize + 1}"
                            )
                        }
                    }
                },
                tag = NestedTag.BUTTON_ADD_ONE_REPEAT_VIEW
            )

            addSpace(16.dp)

            addView(
                navigator.containerView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                120.dp
            )
        }
    }

    private fun createNavigator(
        navController: ViewNavigatorController
    ): ViewNavigator = ViewNavigator(
        context = context,
        name = "NestedNavigator",
        navController = navController,
        initRoute = NestedRoute.REPEAT_VIEW,
        savedStateRegistry = (context as? ComponentActivity)?.savedStateRegistry
    ) {
        navView(
            route = NestedRoute.REPEAT_VIEW,
            cached = false
        ) { entry ->
            frameLayout(context) {
                addView(
                    MaterialCardView(context).apply {
                        cardElevation = 2F.dp
                        strokeWidth = 0
                        addView(
                            linearLayout(context) {
                                addText(
                                    text = entry.args?.getString(TITLE) ?: "RepeatView 1"
                                )
                                addButton(
                                    text = "Pop to Screen 1",
                                    onClick = popToScreenOne,
                                    tag = "${NestedTag.BUTTON_POP_TO_SCREEN_ONE}_${navigator.viewStackSize + 1}"
                                )
                            }, FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = Gravity.CENTER
                            }
                        )
                    },
                    FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(15.dp, 15.dp, 15.dp, 15.dp)
                    }
                )
            }
        }
    }

    override fun update(view: View, entry: ViewStackEntry) {

    }

    override fun onDetach(view: View, entry: ViewStackEntry) {

    }

    override fun onPop(view: View, targetRoute: String?): Boolean {
        var popRet = false
        if (targetRoute == null) {
            popRet = navController?.pop() ?: false
        }
        if (!popRet) {
            popRet = super.onPop(view, targetRoute)
        }
        return popRet
    }
}