package com.gmlive.svgaplayer.intercept

import android.util.Log
import com.gmlive.svgaplayer.ComponentRegistry
import com.gmlive.svgaplayer.EventListener
import com.gmlive.svgaplayer.decode.DataSource
import com.gmlive.svgaplayer.fetch.Fetcher
import com.gmlive.svgaplayer.fetch.SVGAEntityResult
import com.gmlive.svgaplayer.fetch.SourceResult
import com.gmlive.svgaplayer.memory.*
import com.gmlive.svgaplayer.memory.MemoryCacheService
import com.gmlive.svgaplayer.memory.RealMemoryCache
import com.gmlive.svgaplayer.memory.RequestService
import com.gmlive.svgaplayer.memory.StrongMemoryCache
import com.gmlive.svgaplayer.recycle.VideoEntityRefCounter
import com.gmlive.svgaplayer.request.SVGARequest
import com.gmlive.svgaplayer.request.SVGAResult
import com.gmlive.svgaplayer.request.SVGAResult.Metadata
import com.gmlive.svgaplayer.request.SuccessResult
import com.gmlive.svgaplayer.size.OriginalSize
import com.gmlive.svgaplayer.size.PixelSize
import com.gmlive.svgaplayer.size.Size
import com.gmlive.svgaplayer.util.*
import com.gmlive.svgaplayer.util.SystemCallbacks
import com.gmlive.svgaplayer.util.closeQuietly
import com.gmlive.svgaplayer.util.log
import com.gmlive.svgaplayer.util.toDrawable
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGADynamicEntity
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import okhttp3.Call
import kotlin.coroutines.coroutineContext
import kotlin.math.abs

/** The last interceptor in the chain which executes the [SVGARequest]. */
internal class EngineInterceptor(
    private val registry: ComponentRegistry,
    private val referenceCounter: VideoEntityRefCounter,
    private val strongMemoryCache: StrongMemoryCache,
    private val memoryCacheService: MemoryCacheService,
    private val requestService: RequestService,
    private val systemCallbacks: SystemCallbacks,
    private val callFactory: Call.Factory,
    private val logger: Logger?
) : Interceptor {

    override suspend fun intercept(chain: Interceptor.Chain): SVGAResult {
        try {
            // This interceptor uses some internal APIs.
            check(chain is RealInterceptorChain)

            val request = chain.request
            val data = request.data
            val size = chain.size
            val eventListener = chain.eventListener

            val options = requestService.options(request, size, systemCallbacks.isOnline)

            // Perform any data mapping.
            eventListener.mapStart(request, data)
            val mappedData = registry.mapData(data, options)
            eventListener.mapEnd(request, mappedData)

            // Check the memory cache.
            val fetcher = request.fetcher(mappedData) ?: registry.requireFetcher(mappedData)
            val memoryCacheKey =
                request.memoryCacheKey ?: computeMemoryCacheKey(request, mappedData, fetcher, size)
            val value =
                if (request.memoryCachePolicy.readEnabled) memoryCacheService[memoryCacheKey] else null

            val cachedDrawable = value?.videoEntity?.toDrawable()

            // Short circuit if the cached videoEntity is valid.
            if (cachedDrawable != null && isCachedValueValid(
                    memoryCacheKey,
                    value,
                    request,
                    size
                )
            ) {
                val dynamicEntity = request.dynamicEntityBuilder?.create(callFactory, options)
                    ?: SVGADynamicEntity()
                return SuccessResult(
                    drawable = SVGADrawable(value.videoEntity, dynamicEntity),
                    request = request,
                    metadata = Metadata(
                        memoryCacheKey = memoryCacheKey,
                        isSampled = value.isSampled,
                        dataSource = DataSource.MEMORY_CACHE,
                    )
                )
            }

            // Fetch, decode, and cache the videoEntity on a background dispatcher.
            return withContext(request.dispatcher) {
                // Mark the input data as ineligible for pooling (if necessary).
                invalidateData(request.data)

                // Decrement the value from the memory cache if it was not used.
                if (value != null) referenceCounter.decrement(value.videoEntity)

                // Fetch and decode the SVGAVideoEntity.
                val (videoEntity, isSampled, dataSource) =
                    execute(mappedData, fetcher, request, chain.requestType, size, eventListener)

                validateEntity(videoEntity)

                // Cache the result in the memory cache.
                val isCached = writeToMemoryCache(request, memoryCacheKey, videoEntity, isSampled)

                val dynamicEntity = request.dynamicEntityBuilder?.create(callFactory, options)
                    ?: SVGADynamicEntity()

                // Return the result.
                SuccessResult(
                    drawable = SVGADrawable(videoEntity, dynamicEntity),
                    request = request,
                    metadata = Metadata(
                        memoryCacheKey = memoryCacheKey.takeIf { isCached },
                        isSampled = isSampled,
                        dataSource = dataSource
                    )
                )
            }
        } catch (throwable: Throwable) {
            if (throwable is CancellationException) {
                throw throwable
            } else {
                return requestService.errorResult(chain.request, throwable)
            }
        }
    }

    /** Compute the complex cache key for this request. */
    internal fun computeMemoryCacheKey(
        request: SVGARequest,
        data: Any,
        fetcher: Fetcher<Any>,
        size: Size
    ): MemoryCache.Key {
        val base = fetcher.key(data)
        logger?.log(TAG, Log.INFO) {
            "request original key: $base"
        }
        return MemoryCache.Key(base, size, request.parameters)
    }

    /** Return true if [cacheValue] satisfies the [request]. */
    internal fun isCachedValueValid(
        cacheKey: MemoryCache.Key?,
        cacheValue: RealMemoryCache.Value,
        request: SVGARequest,
        size: Size
    ): Boolean {
        // Ensure the size of the cached videoEntity is valid for the request.
        if (!isSizeValid(cacheKey, cacheValue, request, size)) {
            return false
        }

        return true
    }

    /** Return true if [cacheValue]'s size satisfies the [request]. */
    private fun isSizeValid(
        cacheKey: MemoryCache.Key?,
        cacheValue: RealMemoryCache.Value,
        request: SVGARequest,
        size: Size
    ): Boolean {
        when (size) {
            is OriginalSize -> {
                if (cacheValue.isSampled) {
                    logger?.log(TAG, Log.DEBUG) {
                        "${request.data}: Requested original size, but cached image is sampled."
                    }
                    return false
                }
            }
            is PixelSize -> {
                val cachedWidth: Int
                val cachedHeight: Int
                when (val cachedSize = (cacheKey as? MemoryCache.Key.Complex)?.size) {
                    is PixelSize -> {
                        cachedWidth = cachedSize.width
                        cachedHeight = cachedSize.height
                    }
                    OriginalSize, null -> {
                        val entity = cacheValue.videoEntity
                        cachedWidth = entity.videoSize.width.toInt()
                        cachedHeight = entity.videoSize.height.toInt()
                    }
                }

                // Short circuit the size check if the size is at most 1 pixel off in either dimension.
                // This accounts for the fact that downsampling can often produce images with one dimension
                // at most one pixel off due to rounding.
                if (abs(cachedWidth - size.width) <= 1 && abs(cachedHeight - size.height) <= 1) {
                    return true
                }
            }
        }

        return true
    }

    /** Prevent pooling the input data's bitmap. */
    @Suppress("USELESS_CAST")
    private fun invalidateData(data: Any) {
        when (data) {
            is SVGADrawable -> referenceCounter.setValid(data.videoItem, false)
            is SVGAVideoEntity -> referenceCounter.setValid(data, false)
        }
    }

    /** Allow pooling the successful videoItem. */
    private fun validateEntity(entity: SVGAVideoEntity) {
        if (!entity.cleared) {
            // Mark this videoEntity as valid for pooling (if it has not already been made invalid).
            referenceCounter.setValid(entity, true)

            // Eagerly increment the videoEntity's reference count to prevent it being pooled on another thread.
            referenceCounter.increment(entity)
        }
    }

    /** Load the [data] as a [SVGADrawable] */
    private suspend inline fun execute(
        data: Any,
        fetcher: Fetcher<Any>,
        request: SVGARequest,
        type: Int,
        size: Size,
        eventListener: EventListener
    ): SVGAEntityResult {
        val options = requestService.options(request, size, systemCallbacks.isOnline)

        eventListener.fetchStart(request, fetcher, options)
        val fetchResult = fetcher.fetch(data, size, options)
        eventListener.fetchEnd(request, fetcher, options, fetchResult)

        val baseResult = when (fetchResult) {
            is SourceResult -> {
                val decodeResult = try {
                    // Check if we're cancelled.
                    coroutineContext.ensureActive()
                    val decoder = request.decoder ?: registry.requireDecoder(
                        request.data,
                        fetchResult.source,
                        fetchResult.mimeType
                    )
                    // Decode the stream.
                    fetchResult.source.require(4)
                    eventListener.decodeStart(request, decoder, options)
                    val decodeResult = decoder.decode(
                        fetchResult.source,
                        fetcher.key(data),
                        size,
                        options
                    )
                    eventListener.decodeEnd(request, decoder, options, decodeResult)
                    decodeResult
                } catch (throwable: Throwable) {
                    // Only close the stream automatically if there is an uncaught exception.
                    // This allows custom decoders to continue to read the source after returning a drawable.
                    // fetchResult.source.closeQuietly() (原coil逻辑，移植后导致fd泄漏，源码没有看到其他close地方)
                    throw throwable
                } finally {
                    // Close the stream automatically after decoding videoEntity.
                    fetchResult.source.closeQuietly()
                }

                // Combine the fetch and decode operations' results.
                SVGAEntityResult(
                    videoEntity = decodeResult.entity,
                    isSampled = decodeResult.isSampled,
                    dataSource = fetchResult.dataSource
                )
            }
            is SVGAEntityResult -> fetchResult
        }

        // Check if we're cancelled.
        coroutineContext.ensureActive()

        return baseResult
    }

    /** Write the [SVGAVideoEntity] of [drawable] to the memory cache. Return true if it was added
     * to the cache. */
    private fun writeToMemoryCache(
        request: SVGARequest,
        key: MemoryCache.Key?,
        videoEntity: SVGAVideoEntity,
        isSampled: Boolean
    ): Boolean {
        if (!request.memoryCachePolicy.writeEnabled) {
            return false
        }

        if (key != null) {
            if (!videoEntity.cleared) {
                strongMemoryCache.set(key, videoEntity, isSampled)
                return true
            }
        }
        return false
    }

    companion object {
        private const val TAG = "EngineInterceptor"
    }
}
