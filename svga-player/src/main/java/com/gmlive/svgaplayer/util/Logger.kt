package com.gmlive.svgaplayer.util

interface Logger {
    var level: Int

    fun log(tag: String, priority: Int, message: String?, throwable: Throwable?)
}
