package little.goose.navigation

import android.animation.TimeInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator

inline fun animationParams(block: AnimationParams.Builder.() -> Unit): AnimationParams {
    return AnimationParams.Builder().apply(block).build()
}

class AnimationParams private constructor(
    val duration: Long,
    val interpolator: TimeInterpolator? = null,
    val enterAnimations: NavViewAnimation? = null,
    val exitAnimations: NavViewAnimation? = null,
    val popEnterAnimations: NavViewAnimation? = null,
    val popExitAnimations: NavViewAnimation? = null,
) {

    class Builder {

        var duration: Long = 321L

        var interpolator: TimeInterpolator = LinearOutSlowInInterpolator()

        var enterAnimations: NavViewAnimation? = null

        var exitAnimations: NavViewAnimation? = null

        var popEnterAnimations: NavViewAnimation? = null

        var popExitAnimations: NavViewAnimation? = null

        fun build(): AnimationParams {
            val containAnimations = enterAnimations != null
                    || exitAnimations != null
                    || popEnterAnimations != null
                    || popExitAnimations != null

            return AnimationParams(
                duration = duration,
                interpolator = interpolator,
                enterAnimations = enterAnimations
                    ?: (if (containAnimations) NavViewAnimation.EmptyViewAnimation else null),
                exitAnimations = exitAnimations
                    ?: (if (containAnimations) NavViewAnimation.EmptyViewAnimation else null),
                popEnterAnimations = popEnterAnimations
                    ?: (if (containAnimations) NavViewAnimation.EmptyViewAnimation else null),
                popExitAnimations = popExitAnimations
                    ?: (if (containAnimations) NavViewAnimation.EmptyViewAnimation else null)
            )

        }

    }

    companion object {

        const val DEFAULT_DURATION = 321L

        val HorizontalFadeAnimationParams = AnimationParams(
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

        val DefaultAnimationParams = HorizontalAnimationParams

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