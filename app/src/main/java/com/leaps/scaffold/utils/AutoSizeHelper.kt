package com.leaps.scaffold.utils

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.Display
import com.blankj.utilcode.util.ScreenUtils

class AutoSizeHelper {
    class AutoSizeHelper {

        private val sizeInDP = 360

        fun apply(context: Context) {
            context.resources.displayMetrics?.apply {
                if (densityDpi == getStableDensity()) return
                val targetDensity = ScreenUtils.getScreenWidth().toFloat() / sizeInDP
                density = targetDensity
                densityDpi = (targetDensity * DisplayMetrics.DENSITY_MEDIUM).toInt()
            }
        }

        fun cancel(context: Context) {
            context.resources.displayMetrics?.apply {
                density = Resources.getSystem().displayMetrics.density
                densityDpi = Resources.getSystem().displayMetrics.densityDpi
            }
        }

        private fun getStableDensity(): Int {
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                DisplayMetrics.DENSITY_DEVICE_STABLE
            } else {
                try {
                    Reflect.on("android.view.WindowManagerGlobal").call("getWindowManagerService")
                        .call("getInitialDisplayDensity", Display.DEFAULT_DISPLAY).get()
                } catch (e: Exception) {
                    e.printStackTrace()
                    -1
                }
            }
        }

    }
}