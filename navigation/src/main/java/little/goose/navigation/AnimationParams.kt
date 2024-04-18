package little.goose.navigation

import android.animation.TimeInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

class AnimationParams private constructor(
    val duration: Long,
    val interpolator: TimeInterpolator? = null,
    val enterAnimations: List<NavViewAnimation>? = null,
    val exitAnimations: List<NavViewAnimation>? = null,
    val popEnterAnimations: List<NavViewAnimation>? = null,
    val popExitAnimations: List<NavViewAnimation>? = null
) {

    class Builder(private var duration: Long = 400L) {
        private var interpolator: TimeInterpolator = LinearOutSlowInInterpolator()
        private var enterAnimations: MutableList<NavViewAnimation>? = null
        private var exitAnimations: MutableList<NavViewAnimation>? = null
        private var popEnterAnimations: MutableList<NavViewAnimation>? = null
        private var popExitAnimations: MutableList<NavViewAnimation>? = null

        fun addViewNavAnimation(
            enterAnimation: NavViewAnimation,
            exitAnimation: NavViewAnimation,
            popEnterAnimation: NavViewAnimation,
            popExitAnimation: NavViewAnimation
        ) = apply {
            if (enterAnimations == null) {
                enterAnimations = mutableListOf()
            }
            enterAnimations?.add(enterAnimation)
            if (exitAnimations == null) {
                exitAnimations = mutableListOf()
            }
            exitAnimations?.add(exitAnimation)
            if (popEnterAnimations == null) {
                popEnterAnimations = mutableListOf()
            }
            popEnterAnimations?.add(popEnterAnimation)
            if (popExitAnimations == null) {
                popExitAnimations = mutableListOf()
            }
            popExitAnimations?.add(popExitAnimation)
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

        val DefaultAnimationParams = AnimationParams(
            duration = 400,
            interpolator = LinearOutSlowInInterpolator(),
            enterAnimations = listOf(
                NavViewAnimation.HorizontalEnterViewAnimation,
                NavViewAnimation.FadeEnterViewAnimation
            ),
            exitAnimations = listOf(
                NavViewAnimation.HorizontalExitViewAnimation,
                NavViewAnimation.FadeExitViewAnimation
            ),
            popEnterAnimations = listOf(
                NavViewAnimation.HorizontalPopEnterViewAnimation,
                NavViewAnimation.FadePopEnterViewAnimation
            ),
            popExitAnimations = listOf(
                NavViewAnimation.HorizontalPopExitViewAnimation,
                NavViewAnimation.FadePopExitViewAnimation
            ),
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