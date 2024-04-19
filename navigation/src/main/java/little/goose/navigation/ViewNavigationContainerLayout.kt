package little.goose.navigation

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class ViewNavigationContainerLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), ViewContainer<ViewNavigationContainerLayout> {

    init {
        clipToOutline = true
    }

    override val container: ViewNavigationContainerLayout
        get() = this

    override var currentView: View? = null

    private var progressAnimator: ValueAnimator? = null

    override var animationParams = AnimationParams.DefaultAnimationParams

    override fun setView(view: View) {
        changeView(view, animate = false)
    }

    override fun animateChangeView(view: View, forward: Boolean) {
        changeView(view, animate = true, forward = forward)
    }

    private fun changeView(
        view: View,
        animate: Boolean = true,
        forward: Boolean = true
    ) {
        val params = animationParams
        if (!animate
            || params.duration <= 0L
            || params.enterAnimations == null
            || params.exitAnimations == null
            || params.popEnterAnimations == null
            || params.popExitAnimations == null
        ) {
            currentView?.takeIf { it.parent == this }?.let(::removeView)
            addView(
                view,
                LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT
                ).apply {
                    this.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                }
            )
            currentView = view
            return
        }
        this.progressAnimator?.cancel()
        val progressAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            if (params.interpolator != null) {
                interpolator = params.interpolator
            }
        }
        this.progressAnimator = progressAnimator
        currentView?.let { oldView ->
            progressAnimator.addUpdateListener {
                val progress = it.animatedValue as Float
                val exitAnimations = if (forward) {
                    params.popExitAnimations
                } else {
                    params.exitAnimations
                }
                exitAnimations.onAnimate(oldView, progress, width, height)
            }
            progressAnimator.addListener(
                onEnd = { removeView(oldView) },
                onCancel = { removeView(oldView) }
            )
        }
        addView(
            view,
            LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            ).apply {
                this.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            }
        )
        currentView = view
        progressAnimator.addUpdateListener {
            val progress = it.animatedValue as Float
            val enterAnimations = if (forward) {
                params.enterAnimations
            } else {
                params.popEnterAnimations
            }
            enterAnimations.onAnimate(view, progress, width, height)
        }
        progressAnimator.duration = params.duration
        progressAnimator.interpolator = FastOutSlowInInterpolator()
        progressAnimator.start()
    }

}