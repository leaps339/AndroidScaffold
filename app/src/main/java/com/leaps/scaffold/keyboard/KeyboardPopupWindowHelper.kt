package com.leaps.scaffold.keyboard

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.PopupWindow
import com.blankj.utilcode.util.ScreenUtils
import kotlin.math.max

/**
 * author: fanfeiyue
 * time: 2020/7/15
 * des:
 */

class KeyboardPopupWindowHelper(private val mContext: Context) : PopupWindow(),
    ViewTreeObserver.OnGlobalLayoutListener {

    private var contentView: View? = null
    private var maxHeight = 0
    private var onKeyboardStatusChangeListener: OnKeyboardStatusChangeListener? = null
    private var isSoftKeyboardOpened = false

    private val wmParams = WindowManager.LayoutParams().apply {
        type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION
        width = ScreenUtils.getScreenWidth() / 4
        height = WindowManager.LayoutParams.MATCH_PARENT
        flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
    }

    init {
        try {
            release()
//            val wmParams = WindowManager.LayoutParams().apply {
//                type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
//                width = ScreenUtils.getScreenWidth() / 4
//                height = WindowManager.LayoutParams.MATCH_PARENT
//                flags =
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
//                softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//            }
            contentView = View(mContext)
//            (mContext as? Activity)?.windowManager?.addView(contentView?.apply {
//                setBackgroundColor(
//                    Color.GREEN
//                )
//            }, wmParams)

//            isFocusable = false
//            width = ScreenUtils.getScreenWidth() / 2
//            height = LayoutParams.MATCH_PARENT
//            setBackgroundDrawable(ColorDrawable(Color.RED))
//            inputMethodMode = INPUT_METHOD_NEEDED
//            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                windowLayoutType = WindowManager.LayoutParams.TYPE_BASE_APPLICATION
//            }
//            setContentView(contentView)
            contentView?.viewTreeObserver?.addOnGlobalLayoutListener(this)
        } catch (e: Exception) {
            Log.e("KeyboardHelper", "KeyboardPopupWindow init failed:$e")
        }
    }

    fun show(view: View) {
//        showAtLocation(view, Gravity.START, 0, 0)
//        wmParams.token = view.windowToken
        (mContext as? Activity)?.windowManager?.addView(contentView?.apply {
            setBackgroundColor(
                Color.GREEN
            )
        }, wmParams)
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        contentView?.getWindowVisibleDisplayFrame(rect)
        maxHeight = max(maxHeight, rect.bottom)
        val isOpen = maxHeight.minus(rect.bottom) >= ScreenUtils.getScreenHeight().div(5)
        if (isOpen != isSoftKeyboardOpened) {
            isSoftKeyboardOpened = isOpen
            if (isOpen)
                onKeyboardStatusChangeListener?.keyboardOpen(maxHeight.minus(rect.bottom).toFloat())
            else
                onKeyboardStatusChangeListener?.keyboardClose()
        }
    }

    fun setOnKeyboardStatusChangeListener(onKeyboardStatusChangeListener: OnKeyboardStatusChangeListener?) {
        this.onKeyboardStatusChangeListener = onKeyboardStatusChangeListener
    }

    fun release() {
        try {
            contentView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
            onKeyboardStatusChangeListener = null
//            if (contentView?.isAttachedToWindow == true) {
//                (mContext as? Activity)?.windowManager?.removeViewImmediate(contentView)
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

interface OnKeyboardStatusChangeListener {
    fun keyboardOpen(height: Float)
    fun keyboardClose()
}