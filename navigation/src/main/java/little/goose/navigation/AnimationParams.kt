package little.goose.navigation

import android.animation.TimeInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

class AnimationParams private constructor(
    val duration: Long,
    val interpolator: TimeInterpolator? = null,
    val inAnimations: List<NavViewAnimation>? = null,
    val outAnimations: List<NavViewAnimation>? = null
) {

    class Builder(private var duration: Long = 400L) {

        private var interpolator: TimeInterpolator = LinearOutSlowInInterpolator()

        private val inAnimations = mutableListOf<NavViewAnimation>()

        private val outAnimations = mutableListOf<NavViewAnimation>()

        fun addViewNavAnimation(
            inAnimation: NavViewAnimation,
            outAnimation: NavViewAnimation
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
                NavViewAnimation.HorizontalInViewAnimation,
                NavViewAnimation.FadeInViewAnimation
            ),
            outAnimations = listOf(
                NavViewAnimation.HorizontalOutViewAnimation,
                NavViewAnimation.FadeOutViewAnimation
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