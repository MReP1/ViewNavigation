package little.goose.navigation.util

import android.content.Context
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.annotation.ColorInt
import little.goose.navigation.design.Theme

inline fun linearLayout(
    context: Context,
    orientation: Int = LinearLayout.VERTICAL,
    gravity: Int = Gravity.CENTER_HORIZONTAL or Gravity.TOP,
    builder: LinearLayout.() -> Unit
): LinearLayout {
    return LinearLayout(context).apply {
        this.orientation = orientation
        this.gravity = gravity
        builder()
    }
}

inline fun LinearLayout.addText(
    text: String,
    @ColorInt textColor: Int = Theme.Palette.Primary,
    textSize: Float = 16F,
    layoutParams: LinearLayout.LayoutParams.() -> Unit = {}
) {
    addView(
        TextView(context).apply {
            this.textSize = textSize
            setTextColor(textColor)
            this.text = text
        },
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply(layoutParams)
    )
}

inline fun LinearLayout.addButton(
    text: String,
    crossinline onClick: () -> Unit,
    layoutParams: LinearLayout.LayoutParams.() -> Unit = {}
) {
    addView(
        Button(context).apply {
            this.text = text
            setOnClickListener {
                onClick()
            }
        },
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply(layoutParams)
    )
}

fun LinearLayout.addSpace(weight: Float) {
    addView(
        Space(context),
        LinearLayout.LayoutParams(0, 0, weight)
    )
}

fun LinearLayout.addSpace(space: Int) {
    val width: Int
    val height: Int
    when (orientation) {
        LinearLayout.VERTICAL -> {
            width = 0
            height = space
        }

        else -> {
            width = space
            height = 0
        }
    }
    addView(
        Space(context),
        LinearLayout.LayoutParams(width, height)
    )
}