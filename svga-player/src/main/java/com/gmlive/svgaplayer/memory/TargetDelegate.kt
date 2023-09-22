@file:Suppress("NOTHING_TO_INLINE")

package com.gmlive.svgaplayer.memory

import androidx.annotation.MainThread
import com.gmlive.svgaplayer.target.PoolableViewTarget
import com.gmlive.svgaplayer.EventListener
import com.gmlive.svgaplayer.recycle.EmptyVideoEntityRefCounter
import com.gmlive.svgaplayer.recycle.VideoEntityRefCounter
import com.gmlive.svgaplayer.request.ErrorResult
import com.gmlive.svgaplayer.request.SVGAResult
import com.gmlive.svgaplayer.request.SuccessResult
import com.gmlive.svgaplayer.util.Logger
import com.gmlive.svgaplayer.util.requestManager
import com.gmlive.svgaplayer.util.setValid
import com.gmlive.svgaplayer.target.Target
import com.opensource.svgaplayer.SVGAVideoEntity

/**
 * Wrap a [Target] to support [SVGAVideoEntity] pooling.
 *
 * @see DelegateService
 */
internal sealed class TargetDelegate {

    open val target: Target? get() = null

    @MainThread
    open fun start(cached: SVGAVideoEntity?) {
    }

    @MainThread
    open suspend fun success(result: SuccessResult) {
    }

    @MainThread
    open suspend fun error(result: ErrorResult) {
    }

    @MainThread
    open fun clear() {
    }
}

/**
 * An empty target delegate. Used if the request has no target and does not need to invalidate bitmaps.
 */
internal object EmptyTargetDelegate : TargetDelegate()

/**
 * Only invalidate the success bitmap.
 *
 * Used if [SVGARequest.target] is null and the success [Drawable] is exposed.
 *
 * @see SvgaLoader.execute
 */
internal class InvalidatableEmptyTargetDelegate(
    private val referenceCounter: VideoEntityRefCounter
) : TargetDelegate() {

    override suspend fun success(result: SuccessResult) {
        referenceCounter.setValid(result.entity, false)
    }
}

/**
 * Invalidate the cached videoEntity and the success videoEntity.
 */
internal class InvalidateTargetDelegate(
    override val target: Target,
    private val referenceCounter: VideoEntityRefCounter,
    private val eventListener: EventListener,
    private val logger: Logger?
) : TargetDelegate() {

    override fun start(cached: SVGAVideoEntity?) {
        referenceCounter.setValid(cached, false)
        target.onStart()
    }

    override suspend fun success(result: SuccessResult) {
        referenceCounter.setValid(result.entity, false)
        target.onSuccess(result, eventListener, logger)
    }

    override suspend fun error(result: ErrorResult) {
        target.onError(result, eventListener, logger)
    }
}

/**
 * Handle the reference counts for the cached bitmap and the success bitmap.
 */
internal class PoolableTargetDelegate(
    override val target: PoolableViewTarget<*>,
    private val referenceCounter: VideoEntityRefCounter,
    private val eventListener: EventListener,
    private val logger: Logger?
) : TargetDelegate() {

    override fun start(cached: SVGAVideoEntity?) {
        replace(cached) { onStart() }
    }

    override suspend fun success(result: SuccessResult) {
        replace(result.entity) { onSuccess(result, eventListener, logger) }
    }

    override suspend fun error(result: ErrorResult) {
        replace(null) { onError(result, eventListener, logger) }
    }

    override fun clear() {
        replace(null) { onClear() }
    }

    /** Replace the current videoEntity reference with [SVGAVideoEntity]. */
    private inline fun replace(entity: SVGAVideoEntity?, update: PoolableViewTarget<*>.() -> Unit) {
        // Skip reference counting if bitmap pooling is disabled.
        if (referenceCounter is EmptyVideoEntityRefCounter) {
            target.update()
        } else {
            increment(entity)
            target.update()
            decrement(entity)
        }
    }

    /** Increment the reference counter for the current entity. */
    private fun increment(entity: SVGAVideoEntity?) {
        entity?.let(referenceCounter::increment)
    }

    /** Replace the reference to the previous entity and decrement its reference count. */
    private fun decrement(entity: SVGAVideoEntity?) {
        val previous = target.view.requestManager.put(this, entity)
        previous?.let(referenceCounter::decrement)
    }
}

private inline val SVGAResult.entity: SVGAVideoEntity?
    get() = drawable?.videoItem

private inline fun Target.onSuccess(
    result: SuccessResult,
    eventListener: EventListener,
    logger: Logger?
) {
    onSuccess(result.drawable)
}

private inline fun Target.onError(
    result: ErrorResult,
    eventListener: EventListener,
    logger: Logger?
) {
    onError()
}

private const val TAG = "TargetDelegate"
