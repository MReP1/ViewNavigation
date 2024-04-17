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
    private var lastProgress = 0F

    private var animationParams = AnimationParams.DefaultAnimationParams

    override fun setAnimationParams(animationParams: AnimationParams) {
        this.animationParams = animationParams
    }

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
            || params.inAnimations.isNullOrEmpty()
            || params.outAnimations.isNullOrEmpty()
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
                onViewAnimate(oldView, progress, !forward, params.outAnimations)
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
            onViewAnimate(view, progress, forward, params.inAnimations)
            lastProgress = progress
        }
        progressAnimator.duration = params.duration
        progressAnimator.interpolator = FastOutSlowInInterpolator()
        progressAnimator.start()
    }

    private fun onViewAnimate(
        view: View,
        progress: Float,
        forward: Boolean,
        animations: List<ViewNavAnimation>
    ) {
        animations.forEach { animate ->
            animate.onAnimate(view, progress, forward, width, height)
        }
    }

}