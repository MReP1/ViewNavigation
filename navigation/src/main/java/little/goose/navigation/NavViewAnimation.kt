package little.goose.navigation

import android.view.View

fun interface NavViewAnimation {

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

        val HorizontalInViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, forward, width, _ ->
                val distance = width * 1F
                view.translationX = distance * (1 - progress) * (if (forward) 1 else -1)
            }

        val HorizontalOutViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, forward, width, _ ->
                val distance = width * 1F
                view.translationX = distance * progress * (if (forward) 1 else -1)
            }

        val FadeInViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, _, _, _ ->
                view.alpha = progress
            }

        val FadeOutViewAnimation: NavViewAnimation =
            NavViewAnimation { view, progress, _, _, _ ->
                view.alpha = 1 - progress
            }

    }

}