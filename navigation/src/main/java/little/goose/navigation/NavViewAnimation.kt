package little.goose.navigation

import android.util.LayoutDirection
import android.view.View

inline fun alphaViewAnimation(
    crossinline transform: (progress: Float) -> Float
) = NavViewAnimation { view, progress, _, _ -> view.alpha = transform(progress) }

inline fun horizontalViewAnimation(
    crossinline transform: (progress: Float) -> Float
) = horizontalTranslationViewAnimation { progress, width, layoutDirection ->
    width * layoutDirection.ldFlag * transform(progress)
}

inline fun horizontalTranslationViewAnimation(
    crossinline transform: (progress: Float, width: Int, layoutDirection: Int) -> Float
) = NavViewAnimation { view, progress, width, _ ->
    view.translationX = transform(progress, width, view.layoutDirection)
}

inline fun verticalViewAnimation(
    crossinline transform: (progress: Float) -> Float
) = verticalTranslationViewAnimation { progress, height ->
    height * transform(progress)
}

inline fun verticalTranslationViewAnimation(
    crossinline transform: (progress: Float, height: Int) -> Float
) = NavViewAnimation { view, progress, _, height ->
    view.translationY = transform(progress, height)
}

@PublishedApi
internal val Int.ldFlag: Float
    get() = if (this == LayoutDirection.RTL) -1F else 1F

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

        val EmptyViewAnimation: NavViewAnimation = NavViewAnimation { _, _, _, _ -> }

        val HorizontalEnterViewAnimation: NavViewAnimation = horizontalViewAnimation { 1F - it }

        val HorizontalExitViewAnimation: NavViewAnimation = horizontalViewAnimation { it }

        val HorizontalPopEnterViewAnimation: NavViewAnimation = horizontalViewAnimation { it - 1F }

        val HorizontalPopExitViewAnimation: NavViewAnimation = horizontalViewAnimation { -it }

        val VerticalEnterViewAnimation: NavViewAnimation = verticalViewAnimation { 1F - it }

        val VerticalExitViewAnimation: NavViewAnimation = verticalViewAnimation { it }

        val VerticalPopEnterViewAnimation: NavViewAnimation = verticalViewAnimation { it - 1F }

        val VerticalPopExitViewAnimation: NavViewAnimation = verticalViewAnimation { -it }

        val FadeEnterViewAnimation: NavViewAnimation = alphaViewAnimation { it }

        val FadeExitViewAnimation: NavViewAnimation = alphaViewAnimation { 1F - it }

        val FadePopEnterViewAnimation: NavViewAnimation = alphaViewAnimation { it }

        val FadePopExitViewAnimation: NavViewAnimation = alphaViewAnimation { 1F - it }

    }

}