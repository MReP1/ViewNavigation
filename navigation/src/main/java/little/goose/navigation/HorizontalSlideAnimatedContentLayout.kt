package little.goose.navigation

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.animation.addListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

class HorizontalSlideAnimatedContentLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs), ViewContainer<HorizontalSlideAnimatedContentLayout> {

    override val container: HorizontalSlideAnimatedContentLayout get() = this

    override var currentView: View? = null

    private var progressAnimator: ValueAnimator? = null
    private var lastFlag = 1

    init {
        clipToOutline = true
    }

    override fun setView(view: View) {
        changeView(view, animate = false)
    }

    override fun animateChangeView(view: View, forward: Boolean) {
        changeView(view, animate = true, forward = forward)
    }

    private fun setView(view: View, gravity: Int = Gravity.TOP or Gravity.CENTER_HORIZONTAL) {
        currentView?.takeIf { it.parent == this }?.let(::removeView)
        addView(view, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT).apply {
            this.gravity = gravity
        })
        currentView = view
    }

    private fun changeView(
        view: View,
        gravity: Int = Gravity.TOP or Gravity.CENTER_HORIZONTAL,
        animate: Boolean = true,
        duration: Long = 400,
        forward: Boolean = true,
        animateAlpha: Boolean = false
    ) {
        if (!animate) {
            setView(view, gravity)
            return
        }
        this.progressAnimator?.cancel()
        val progressAnimator = ValueAnimator.ofFloat(0F, 1F)
        this.progressAnimator = progressAnimator
        val flag = if (forward) 1 else -1
        currentView?.let { oldView ->
            val currentTransactionX = oldView.translationX * lastFlag
            val currentAlpha = oldView.alpha
            progressAnimator.addUpdateListener {
                val progress = it.animatedValue as Float
                oldView.translationX = -((currentTransactionX + width) * progress) * flag
                if (animateAlpha) {
                    oldView.alpha = (currentAlpha) * (1 - progress)
                }
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
                this.gravity = gravity
            }
        )
        currentView = view
        view.translationX = width.toFloat()
        if (animateAlpha) {
            view.alpha = 0F
        }
        progressAnimator.addUpdateListener {
            val progress = it.animatedValue as Float
            view.translationX = width * (1 - progress) * flag
            if (animateAlpha) {
                view.alpha = progress
            }
        }
        progressAnimator.duration = duration
        progressAnimator.interpolator = FastOutSlowInInterpolator()
        progressAnimator.start()
        lastFlag = flag
    }

}