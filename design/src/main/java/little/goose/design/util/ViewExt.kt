package little.goose.design.util

import android.content.Context
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.annotation.ColorInt
import com.google.android.material.button.MaterialButton

inline fun linearLayout(
    context: Context,
    orientation: Int = LinearLayout.VERTICAL,
    gravity: Int = Gravity.CENTER_HORIZONTAL or Gravity.TOP,
    tag: Any? = null,
    builder: LinearLayout.() -> Unit
): LinearLayout {
    return LinearLayout(context).apply {
        this.orientation = orientation
        this.gravity = gravity
        if (tag != null) {
            this.tag = tag
        }
        builder()
    }
}

inline fun frameLayout(
    context: Context,
    foregroundGravity: Int = Gravity.CENTER,
    tag: Any? = null,
    builder: FrameLayout.() -> Unit
): FrameLayout {
    return FrameLayout(context).apply {
        this.foregroundGravity = foregroundGravity
        if (tag != null) {
            this.tag = tag
        }
        builder()
    }
}

inline fun LinearLayout.addText(
    text: String,
    @ColorInt textColor: Int = little.goose.design.theme.Theme.Palette.Primary,
    textSize: Float = 16F,
    tag: Any? = null,
    layoutParams: LinearLayout.LayoutParams.() -> Unit = {}
): TextView {
    val textView = TextView(context).apply {
        this.textSize = textSize
        setTextColor(textColor)
        this.text = text
        if (tag != null) {
            this.tag = tag
        }
    }
    addView(
        textView, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply(layoutParams)
    )
    return textView
}

inline fun LinearLayout.addButton(
    text: String,
    crossinline onClick: () -> Unit,
    tag: Any? = null,
    layoutParams: LinearLayout.LayoutParams.() -> Unit = {}
) {
    addView(
        MaterialButton(context).apply {
            this.text = text
            if (tag != null) {
                this.tag = tag
            }
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

fun LinearLayout.addSpace(
    weight: Float,
    tag: Any? = null,
) {
    addView(
        Space(context).apply {
            if (tag != null) {
                this.tag = tag
            }
        },
        LinearLayout.LayoutParams(0, 0, weight)
    )
}

fun LinearLayout.addSpace(
    space: Int,
    tag: Any? = null,
) {
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
        Space(context).apply {
            if (tag != null) {
                this.tag = tag
            }
        },
        LinearLayout.LayoutParams(width, height)
    )
}