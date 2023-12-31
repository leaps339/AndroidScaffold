@file:Suppress("unused")

package com.gmlive.svgaplayer.map

import com.gmlive.svgaplayer.decode.Options
import com.gmlive.svgaplayer.fetch.Fetcher

/**
 * An interface to convert data of type [T] into [V].
 * Use this to map custom data types to a type that can be handled by a [Fetcher].
 * @Author svenj
 * @Date 2020/11/25
 * @Email svenjzm@gmail.com
 */
interface Mapper<T : Any, V : Any> {

    /** Return true if this can convert [data]. */
    fun handles(data: T): Boolean = true

    /** Convert [data] into [V]. */
    fun map(data: T, options: Options): V?
}
