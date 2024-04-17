package little.goose.navigation

import android.animation.TimeInterpolator
import android.view.View
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

fun interface ViewNavAnimation {

    /**
     * On animate
     *
     * @param view the view you need to operate.
     * @param progress from 0 to 1.
     * @param forward flag that is forward or not.
     * @param width with of parent.
     * @param height height of parent.
     */
    fun onAnimate(
        view: View,
        progress: Float,
        forward: Boolean,
        width: Int,
        height: Int
    )

    companion object {

        val HorizontalInViewAnimation: ViewNavAnimation =
            ViewNavAnimation { view, progress, forward, width, _ ->
                val distance = width * 1F
                view.translationX = distance * (1 - progress) * (if (forward) 1 else -1)
            }

        val HorizontalOutViewAnimation: ViewNavAnimation =
            ViewNavAnimation { view, progress, forward, width, _ ->
                val distance = width * 1F
                view.translationX = distance * progress * (if (forward) 1 else -1)
            }

        val FadeInViewAnimation: ViewNavAnimation =
            ViewNavAnimation { view, progress, _, _, _ ->
                view.alpha = progress
            }

        val FadeOutViewAnimation: ViewNavAnimation =
            ViewNavAnimation { view, progress, _, _, _ ->
                view.alpha = 1 - progress
            }

    }

}

class AnimationParams private constructor(
    val duration: Long,
    val interpolator: TimeInterpolator? = null,
    val inAnimations: List<ViewNavAnimation>? = null,
    val outAnimations: List<ViewNavAnimation>? = null
) {

    class Builder(private var duration: Long = 400L) {

        private var interpolator: TimeInterpolator = LinearOutSlowInInterpolator()

        private val inAnimations = mutableListOf<ViewNavAnimation>()

        private val outAnimations = mutableListOf<ViewNavAnimation>()

        fun addViewNavAnimation(
            inAnimation: ViewNavAnimation,
            outAnimation: ViewNavAnimation
        ) = apply {
            inAnimations.add(inAnimation)
            outAnimations.add(outAnimation)
        }

        fun setDuration(duration: Long) {
            require(duration >= 0)
            this.duration = duration
        }

        fun setInterpolator(interpolator: TimeInterpolator) {
            this.interpolator = interpolator
        }

        fun build() = AnimationParams(
            duration = duration,
            interpolator = interpolator,
            inAnimations = inAnimations,
            outAnimations = outAnimations
        )

    }

    override fun toString(): String {
        return "[AnimationParams duration: $duration, " +
                "inAnimation size ${inAnimations?.size ?: 0} " +
                "outAnimation size ${outAnimations?.size ?: 0}]"
    }

    companion object {

        val DefaultAnimationParams = AnimationParams(
            duration = 400,
            interpolator = LinearOutSlowInInterpolator(),
            inAnimations = listOf(
                ViewNavAnimation.HorizontalInViewAnimation,
                ViewNavAnimation.FadeInViewAnimation
            ),
            outAnimations = listOf(
                ViewNavAnimation.HorizontalOutViewAnimation,
                ViewNavAnimation.FadeOutViewAnimation
            )
        )

        val NoAnimationParams = AnimationParams(
            duration = 0L,
            interpolator = null,
            inAnimations = null,
            outAnimations = null
        )

    }
}