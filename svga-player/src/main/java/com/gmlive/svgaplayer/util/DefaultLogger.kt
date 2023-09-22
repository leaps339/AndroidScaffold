package com.gmlive.svgaplayer.util

import android.util.Log

/**

 * @Author svenj
 * @Date 2020/11/27
 * @Email svenjzm@gmail.com
 */
class DefaultLogger: Logger {
    override var level: Int = Log.ERROR
    override fun log(tag: String, priority: Int, message: String?, throwable: Throwable?) {
        Log.i(tag, message, throwable)
    }
}