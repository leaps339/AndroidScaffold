@file:JvmName("-Requests")
@file:Suppress("NOTHING_TO_INLINE")

package com.gmlive.svgaplayer.util

import com.gmlive.svgaplayer.fetch.Fetcher
import com.gmlive.svgaplayer.request.SVGARequest

/** Ensure [SVGARequest.fetcher] is valid for [data]. */
@Suppress("UNCHECKED_CAST")
internal fun <T : Any> SVGARequest.fetcher(data: T): Fetcher<T>? {
    val (fetcher, type) = fetcher ?: return null
    check(type.isAssignableFrom(data::class.java)) {
        "${fetcher.javaClass.name} cannot handle data with type ${data.javaClass.name}."
    }
    return fetcher as Fetcher<T>
}
