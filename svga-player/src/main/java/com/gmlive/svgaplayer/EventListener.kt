package com.gmlive.svgaplayer

import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.gmlive.svgaplayer.decode.DecodeResult
import com.gmlive.svgaplayer.decode.Decoder
import com.gmlive.svgaplayer.decode.Options
import com.gmlive.svgaplayer.fetch.FetchResult
import com.gmlive.svgaplayer.fetch.Fetcher
import com.gmlive.svgaplayer.request.SVGARequest
import com.gmlive.svgaplayer.request.SVGAResult
import com.gmlive.svgaplayer.size.Size

interface EventListener : SVGARequest.Listener {

    @MainThread
    override fun onStart(request: SVGARequest) {}

    @MainThread
    fun resolveSizeStart(request: SVGARequest) {}

    @MainThread
    fun resolveSizeEnd(request: SVGARequest, size: Size) {}

    @AnyThread
    fun mapStart(request: SVGARequest, input: Any) {}

    @AnyThread
    fun mapEnd(request: SVGARequest, output: Any) {}

    @WorkerThread
    fun fetchStart(request: SVGARequest, fetcher: Fetcher<*>, options: Options) {}

    @WorkerThread
    fun fetchEnd(request: SVGARequest, fetcher: Fetcher<*>, options: Options, result: FetchResult) {}

    @WorkerThread
    fun decodeStart(request: SVGARequest, decoder: Decoder, options: Options) {}

    @WorkerThread
    fun decodeEnd(request: SVGARequest, decoder: Decoder, options: Options, result: DecodeResult) {}

    @MainThread
    override fun onCancel(request: SVGARequest) {}

    @MainThread
    override fun onError(request: SVGARequest, throwable: Throwable) {}

    @MainThread
    override fun onSuccess(request: SVGARequest, metadata: SVGAResult.Metadata) {}

    /** A factory that creates new [EventListener] instances. */
    fun interface Factory {

        companion object {
            @JvmField val NONE = Factory(EventListener.NONE)

            /** Create an [EventListener.Factory] that always returns [listener]. */
            @JvmStatic
            @JvmName("create")
            operator fun invoke(listener: EventListener) = Factory { listener }
        }

        /** Return a new [EventListener]. */
        fun create(request: SVGARequest): EventListener
    }

    companion object {
        @JvmField val NONE = object : EventListener {}
    }
}
