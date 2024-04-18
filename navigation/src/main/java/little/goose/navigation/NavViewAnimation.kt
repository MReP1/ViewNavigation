package little.goose.navigation

import android.util.LayoutDirection
import android.view.View

fun interface NavViewAnimation {

    /**
     * On animate
     *
     * @param view the view you need to operate.
     * @param progress from 0 to 1.
     * @param width with of parent.
     * @param height height of parent.
     */
    fun onAnimate(
        view: View,
        progress: Float,
        width: Int,
        height: Int
    )

    companion object {

        private val View.layoutDirectionFlag: Float
            get() = if (
                layoutDirection == LayoutDirection.RTL
            ) -1F else 1F

        val HorizontalEnterViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, width, _ ->
                val distance = width * 1F * view.layoutDirectionFlag
                view.translationX = distance * (1F - progress)
            }

        val HorizontalExitViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, width, _ ->
                val distance = width * 1F * view.layoutDirectionFlag
                view.translationX = distance * progress
            }

        val HorizontalPopEnterViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, width, _ ->
                val distance = width * -1F * view.layoutDirectionFlag
                view.translationX = distance * (1F - progress)
            }

        val HorizontalPopExitViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, width, _ ->
                val distance = width * -1F * view.layoutDirectionFlag
                view.translationX = distance * progress
            }

        val FadeEnterViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, _, _ ->
                view.alpha = progress
            }

        val FadeExitViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, _, _ ->
                view.alpha = 1F - progress
            }

        val FadePopEnterViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, _, _ ->
                view.alpha = progress
            }

        val FadePopExitViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, _, _ ->
                view.alpha = 1F - progress
            }

    }

}