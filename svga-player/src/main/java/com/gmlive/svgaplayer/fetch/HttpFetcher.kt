package com.gmlive.svgaplayer.fetch

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.annotation.VisibleForTesting
import com.gmlive.svgaplayer.decode.DataSource
import com.gmlive.svgaplayer.decode.Options
import com.gmlive.svgaplayer.network.HttpException
import com.gmlive.svgaplayer.size.Size
import com.gmlive.svgaplayer.util.await
import com.gmlive.svgaplayer.util.getMimeTypeFromUrl
import com.gmlive.svgaplayer.util.md5Key
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.ResponseBody

internal class HttpUriFetcher(callFactory: Call.Factory) : HttpFetcher<Uri>(callFactory) {

    override fun handles(data: Uri) = data.scheme == "http" || data.scheme == "https"

    override fun key(data: Uri) = data.toString().md5Key()

    override fun Uri.toHttpUrl(): HttpUrl = HttpUrl.get(toString())
}

internal class HttpUrlFetcher(callFactory: Call.Factory) : HttpFetcher<HttpUrl>(callFactory) {

    override fun key(data: HttpUrl) = data.toString().md5Key()

    override fun HttpUrl.toHttpUrl(): HttpUrl = this
}

internal abstract class HttpFetcher<T : Any>(private val callFactory: Call.Factory) : Fetcher<T> {

    /**
     * Perform this conversion in a [Fetcher] instead of a [Mapper] so
     * [HttpUriFetcher] can execute [HttpUrl.get] on a background thread.
     */
    abstract fun T.toHttpUrl(): HttpUrl

    override suspend fun fetch(
        data: T,
        size: Size,
        options: Options
    ): FetchResult {
        val url = data.toHttpUrl()
        val request = Request.Builder().url(url).headers(options.headers)

        val networkRead = options.networkCachePolicy.readEnabled
        val diskRead = options.diskCachePolicy.readEnabled
        when {
            !networkRead && diskRead -> {
                request.cacheControl(CacheControl.FORCE_CACHE)
            }
            networkRead && !diskRead -> if (options.diskCachePolicy.writeEnabled) {
                request.cacheControl(CacheControl.FORCE_NETWORK)
            } else {
                request.cacheControl(CACHE_CONTROL_FORCE_NETWORK_NO_CACHE)
            }
            !networkRead && !diskRead -> {
                // This causes the request to fail with a 504 Unsatisfiable Request.
                request.cacheControl(CACHE_CONTROL_NO_NETWORK_NO_CACHE)
            }
        }

        val response = callFactory.newCall(request.build()).await()
        if (!response.isSuccessful) {
            response.body()?.close()
            throw HttpException(response)
        }
        val body = checkNotNull(response.body()) { "Null response body!" }

        return SourceResult(
            source = body.source(),
            mimeType = getMimeType(url, body),
            dataSource = if (response.cacheResponse() != null) DataSource.DISK else DataSource.NETWORK
        )
    }

    /**
     * Parse the response's `content-type` header.
     *
     * "text/plain" is often used as a default/fallback MIME type.
     * Attempt to guess a better MIME type from the file extension.
     */
    @VisibleForTesting
    internal fun getMimeType(data: HttpUrl, body: ResponseBody): String? {
        val rawContentType = body.contentType()?.toString()
        if (rawContentType == null || rawContentType.startsWith(MIME_TYPE_TEXT_PLAIN)) {
            MimeTypeMap.getSingleton().getMimeTypeFromUrl(data.toString())?.let { return it }
        }
        return rawContentType?.substringBefore(';')
    }

    companion object {
        private const val MIME_TYPE_TEXT_PLAIN = "text/plain"

        val CACHE_CONTROL_FORCE_NETWORK_NO_CACHE: CacheControl =
            CacheControl.Builder().noCache().noStore().build()
        val CACHE_CONTROL_NO_NETWORK_NO_CACHE: CacheControl =
            CacheControl.Builder().noCache().onlyIfCached().build()
    }
}
