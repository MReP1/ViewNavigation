package little.goose.navigation.app.component

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import little.goose.navigation.ViewController
import little.goose.navigation.ViewStackEntry
import little.goose.navigation.app.MainActivity
import little.goose.navigation.app.MainTag
import little.goose.navigation.app.MainViewModel
import little.goose.design.theme.Theme
import little.goose.design.util.addButton
import little.goose.design.util.addSpace
import little.goose.design.util.dp
import little.goose.design.util.linearLayout

class CoroutineSampleViewController(
    private val activity: MainActivity,
    private val pop: () -> Unit,
    private val navigateToScreenOne: () -> Unit,
    private val popToOneWithParams: () -> Unit,
    private val navigateToNestedScreen: () -> Unit
) : ViewController<View>() {

    private val viewModel by activity.viewModels<MainViewModel>()

    private val tvCount by lazy {
        TextView(activity).apply {
            setTextColor(Theme.Palette.Primary)
            textSize = 28F
            tag = MainTag.ScreenThree.TITLE
        }
    }

    override fun buildView(entry: ViewStackEntry): View {
        return linearLayout(activity) {
            addSpace(16.dp)
            addView(
                tvCount,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(16.dp, 16.dp, 16.dp, 16.dp)
                }
            )
            addSpace(1F)
            addButton(
                text = "pop",
                onClick = pop
            )
            addButton(
                text = "pop to screen one with params",
                onClick = popToOneWithParams,
                tag = MainTag.ScreenThree.BUTTON_BACK_TO_SCREEN_ONE
            )
            addButton(
                text = "navigate to NestedViewNavigation",
                onClick = navigateToNestedScreen,
                tag = MainTag.ScreenThree.BUTTON_NAVIGATE_TO_NESTED
            )
            addButton(
                text = "navigate to Screen one",
                onClick = navigateToScreenOne
            )
            addSpace(60.dp)
        }
    }

    override fun update(view: View, entry: ViewStackEntry) {
        // We can launch a coroutine with a job from entry.
        activity.lifecycleScope.launch(entry.job) {
            viewModel.countStateFlow.collectLatest {
                tvCount.text = "collect count $it"
            }
        }
    }

    override fun onDetach(view: View, entry: ViewStackEntry) {

    }

    override fun onPop(view: View, targetRoute: String?): Boolean {
        return super.onPop(view, targetRoute)
    }
}