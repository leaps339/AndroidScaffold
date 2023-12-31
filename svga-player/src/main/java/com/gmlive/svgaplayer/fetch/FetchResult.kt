package com.gmlive.svgaplayer.fetch

import android.graphics.drawable.Drawable
import com.gmlive.svgaplayer.decode.DataSource
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAVideoEntity
import okio.BufferedSource

/** The result of [Fetcher.fetch]. */
sealed class FetchResult

/**
 * A raw [BufferedSource] result, which will be consumed by the relevant [Decoder].
 *
 * @param source An unconsumed [BufferedSource] that will be decoded by a [Decoder].
 * @param mimeType An optional MIME type for the [source].
 * @param dataSource Where [source] was loaded from.
 */
data class SourceResult(
    val source: BufferedSource,
    val mimeType: String?,
    val dataSource: DataSource
) : FetchResult()

/**
 * A direct [Drawable] result. Return this from a [Fetcher] if its data cannot be converted into a [BufferedSource].
 *
 * @param drawable The loaded [Drawable].
 * @param isSampled True if [drawable] is sampled (i.e. not loaded into memory at full size).
 * @param dataSource The source that [drawable] was fetched from.
 */
data class SVGAEntityResult(
    val videoEntity: SVGAVideoEntity,
    val isSampled: Boolean,
    val dataSource: DataSource
) : FetchResult()
