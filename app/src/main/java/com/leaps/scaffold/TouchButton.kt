package com.leaps.scaffold

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.withClip

class TouchButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatButton(context, attrs, defStyle) {

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> Log.i("davhdaiva", "down")
            MotionEvent.ACTION_MOVE -> Log.i("davhdaiva", "move")
            MotionEvent.ACTION_UP -> Log.i("davhdaiva", "up")
            MotionEvent.ACTION_CANCEL -> Log.i("davhdaiva", "cancel")
        }
        return super.onTouchEvent(event)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

}