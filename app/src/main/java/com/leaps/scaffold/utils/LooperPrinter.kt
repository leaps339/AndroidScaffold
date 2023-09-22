package com.leaps.scaffold.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Printer

const val TAG = "looper_monitor"

/**
 * 默认卡顿阈值
 */
const val DEFAULT_BLOCK_THRESHOLD_MILLIS = 1000L
const val BEGIN_TAG = ">>>>> Dispatching"
const val END_TAG = "<<<<< Finished"

class LooperPrinter : Printer {

    private var mBeginTime = 0L

    @Volatile
    var mHasEnd = false
    private val collectRunnable by lazy { CollectRunnable() }
    private val handlerThreadWrapper by lazy { HandlerThreadWrapper() }

    override fun println(msg: String?) {
        if (msg.isNullOrEmpty()) {
            return
        }
//        Log.i(TAG, "$msg")
        if (msg.startsWith(BEGIN_TAG)) {
            mBeginTime = System.currentTimeMillis()
            mHasEnd = false

            //需要单独搞个线程来获取堆栈
            handlerThreadWrapper.handler.postDelayed(
                collectRunnable,
                DEFAULT_BLOCK_THRESHOLD_MILLIS
            )
        } else {
            mHasEnd = true
            if (System.currentTimeMillis() - mBeginTime < DEFAULT_BLOCK_THRESHOLD_MILLIS) {
                handlerThreadWrapper.handler.removeCallbacks(collectRunnable)
            }
        }
    }

    fun getMainThreadStackTrace(): String {
        val stackTrace = Looper.getMainLooper().thread.stackTrace
        return StringBuilder().apply {
            for (stackTraceElement in stackTrace) {
                append(stackTraceElement.toString())
                append("\n")
            }
        }.toString()
    }

    inner class CollectRunnable : Runnable {
        override fun run() {
            if (!mHasEnd) {
                //主线程堆栈给拿出来，打印一下
                Log.i(TAG, getMainThreadStackTrace())
            }
        }
    }

    class HandlerThreadWrapper {
        var handler: Handler
        init {
            val handlerThread = HandlerThread("LooperHandlerThread")
            handlerThread.start()
            handler = Handler(handlerThread.looper)
        }
    }

}