package little.goose.navigation.app.component

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import little.goose.navigation.app.MainActivity
import little.goose.navigation.app.MainViewModel
import little.goose.navigation.ViewController
import little.goose.navigation.ViewStackEntry
import little.goose.navigation.app.MainTag
import little.goose.navigation.app.design.Theme
import little.goose.navigation.app.util.addButton
import little.goose.navigation.app.util.dp

class CoroutineSampleViewController(
    private val activity: MainActivity,
    private val popToOneWithParams: () -> Unit
) : ViewController<View>() {

    private val viewModel by activity.viewModels<MainViewModel>()

    private val tvCount by lazy {
        TextView(activity).apply {
            setTextColor(Theme.Palette.Primary)
        }
    }

    override fun buildView(entry: ViewStackEntry): View {
        return LinearLayout(activity).apply {
            gravity = Gravity.CENTER
            orientation = LinearLayout.VERTICAL
            addView(
                tvCount,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(16.dp, 16.dp, 16.dp, 16.dp) }
            )
            addButton(
                text = "pop to screen one with params",
                onClick = popToOneWithParams,
                tag = MainTag.ScreenThree.BUTTON_BACK_TO_SCREEN_ONE
            )
        }
    }

    override fun update(view: View, entry: ViewStackEntry) {
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