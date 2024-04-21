package little.goose.design.util

import android.content.Context
import android.view.View
import kotlin.math.roundToInt

context(View)
val Int.dp: Int
    get() = context.getDpI(this)

context(View)
val Float.dp: Float
    get() = context.getDpF(this)

private fun Context?.getDpI(value: Int): Int {
    this ?: return 0
    return (this.resources.displayMetrics.density * value).roundToInt()
}

private fun Context?.getDpF(value: Float): Float {
    this ?: return 0F
    return this.resources.displayMetrics.density * value
}