package little.goose.navigation

import android.util.LayoutDirection
import android.view.View

inline fun fadeViewAnimation(
    crossinline transform: (progress: Float) -> Float
) = NavViewAnimation { view, progress, _, _ -> view.alpha = transform(progress) }

inline fun horizontalViewAnimation(
    crossinline transform: (progress: Float, width: Int, layoutDirection: Int) -> Float
) = NavViewAnimation { view, progress, width, _ ->
    view.translationX = transform(progress, width, view.layoutDirection)
}

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

    operator fun plus(other: NavViewAnimation): NavViewAnimation {
        return NavViewAnimation { view, progress, width, height ->
            this.onAnimate(view, progress, width, height)
            other.onAnimate(view, progress, width, height)
        }
    }

    companion object {

        private val Int.ldFlag: Float
            get() = if (this == LayoutDirection.RTL) -1F else 1F


        val HorizontalEnterViewAnimation: NavViewAnimation =
            horizontalViewAnimation { progress, width, layoutDirection ->
                width * 1F * layoutDirection.ldFlag * (1F - progress)
            }

        val HorizontalExitViewAnimation: NavViewAnimation =
            horizontalViewAnimation { progress, width, layoutDirection ->
                width * 1F * layoutDirection.ldFlag * progress
            }

        val HorizontalPopEnterViewAnimation: NavViewAnimation =
            horizontalViewAnimation { progress, width, layoutDirection ->
                width * -1F * layoutDirection.ldFlag * (1F - progress)
            }

        val HorizontalPopExitViewAnimation: NavViewAnimation =
            horizontalViewAnimation { progress, width, layoutDirection ->
                width * -1F * layoutDirection.ldFlag * progress
            }

        val FadeEnterViewAnimation: NavViewAnimation = fadeViewAnimation { it }

        val FadeExitViewAnimation: NavViewAnimation = fadeViewAnimation { 1F - it }

        val FadePopEnterViewAnimation: NavViewAnimation = fadeViewAnimation { it }

        val FadePopExitViewAnimation: NavViewAnimation = fadeViewAnimation { 1F - it }
    }

}