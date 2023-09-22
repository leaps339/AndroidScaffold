package com.gmlive.svgaplayer.fetch

import com.gmlive.svgaplayer.decode.Options
import com.gmlive.svgaplayer.size.Size

/**
 *
 * @Author svenj
 * @Date 2020/11/25
 * @Email svenjzm@gmail.com
 */
interface Fetcher<T : Any> {

    fun handles(data: T): Boolean = true

    fun key(data: T): String

    suspend fun fetch(data: T, size: Size, options: Options): FetchResult
}