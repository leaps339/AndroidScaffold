package com.gmlive.svgaplayer.memory

import androidx.annotation.WorkerThread
import com.gmlive.svgaplayer.decode.Options
import com.gmlive.svgaplayer.request.CachePolicy
import com.gmlive.svgaplayer.request.ErrorResult
import com.gmlive.svgaplayer.request.SVGARequest
import com.gmlive.svgaplayer.size.Size
import com.gmlive.svgaplayer.util.Logger

/** Handles operations that act on [SVGARequest]s. */
internal class RequestService(private val logger: Logger?) {

    fun errorResult(request: SVGARequest, throwable: Throwable): ErrorResult {
        return ErrorResult(
            drawable = null,
            request = request,
            throwable = throwable
        )
    }

    @WorkerThread
    fun options(
        request: SVGARequest,
        size: Size,
        isOnline: Boolean
    ): Options {
        // Disable fetching from the network if we know we're offline.
        val networkCachePolicy = if (isOnline) request.networkCachePolicy else CachePolicy.DISABLED

        // Disable allowRgb565 if there are transformations or the requested config is ALPHA_8.
        // ALPHA_8 is a mask config where each pixel is 1 byte so it wouldn't make sense to use RGB_565 as an optimization in that case.

        return Options(
            context = request.context,
            headers = request.headers,
            parameters = request.parameters,
            memoryCachePolicy = request.memoryCachePolicy,
            diskCachePolicy = request.diskCachePolicy,
            networkCachePolicy = networkCachePolicy
        )
    }

}
