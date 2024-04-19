package little.goose.navigation

import android.animation.TimeInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

class AnimationParams private constructor(
    val duration: Long,
    val interpolator: TimeInterpolator? = null,
    val enterAnimations: NavViewAnimation? = null,
    val exitAnimations: NavViewAnimation? = null,
    val popEnterAnimations: NavViewAnimation? = null,
    val popExitAnimations: NavViewAnimation? = null,
) {

    class Builder(private var duration: Long = 400L) {
        private var interpolator: TimeInterpolator = LinearOutSlowInInterpolator()
        private var enterAnimations: NavViewAnimation? = null
        private var exitAnimations: NavViewAnimation? = null
        private var popEnterAnimations: NavViewAnimation? = null
        private var popExitAnimations: NavViewAnimation? = null

        fun addViewNavAnimation(
            enterAnimation: NavViewAnimation,
            exitAnimation: NavViewAnimation,
            popEnterAnimation: NavViewAnimation,
            popExitAnimation: NavViewAnimation
        ) = apply {
            enterAnimations =
                enterAnimations?.let { it + enterAnimation } ?: enterAnimation
            exitAnimations =
                exitAnimations?.let { it + exitAnimation } ?: exitAnimation
            popEnterAnimations =
                popEnterAnimations?.let { it + popEnterAnimation } ?: popEnterAnimation
            popExitAnimations =
                popExitAnimations?.let { it + popExitAnimation } ?: popExitAnimation
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
            enterAnimations = enterAnimations,
            exitAnimations = exitAnimations,
            popEnterAnimations = popEnterAnimations,
            popExitAnimations = popExitAnimations
        )

    }

    companion object {

        const val DEFAULT_DURATION = 321L

        val DefaultAnimationParams = AnimationParams(
            duration = DEFAULT_DURATION,
            interpolator = LinearOutSlowInInterpolator(),
            enterAnimations = NavViewAnimation.HorizontalEnterViewAnimation
                    + NavViewAnimation.FadeEnterViewAnimation,
            exitAnimations = NavViewAnimation.HorizontalExitViewAnimation
                    + NavViewAnimation.FadeExitViewAnimation,
            popEnterAnimations = NavViewAnimation.HorizontalPopEnterViewAnimation
                    + NavViewAnimation.FadePopEnterViewAnimation,
            popExitAnimations = NavViewAnimation.HorizontalPopExitViewAnimation
                    + NavViewAnimation.FadePopExitViewAnimation
        )

        val HorizontalAnimationParams = AnimationParams(
            duration = DEFAULT_DURATION,
            interpolator = LinearOutSlowInInterpolator(),
            enterAnimations = NavViewAnimation.HorizontalEnterViewAnimation,
            exitAnimations = NavViewAnimation.HorizontalExitViewAnimation,
            popEnterAnimations = NavViewAnimation.HorizontalPopEnterViewAnimation,
            popExitAnimations = NavViewAnimation.HorizontalPopExitViewAnimation
        )

        val FadeAnimationParams = AnimationParams(
            duration = DEFAULT_DURATION,
            interpolator = LinearOutSlowInInterpolator(),
            enterAnimations = NavViewAnimation.FadeEnterViewAnimation,
            exitAnimations = NavViewAnimation.FadeExitViewAnimation,
            popEnterAnimations = NavViewAnimation.FadePopEnterViewAnimation,
            popExitAnimations = NavViewAnimation.FadePopExitViewAnimation
        )

        val NoAnimationParams = AnimationParams(
            duration = 0L,
            interpolator = null,
            enterAnimations = null,
            popExitAnimations = null,
            exitAnimations = null,
            popEnterAnimations = null
        )

    }
}