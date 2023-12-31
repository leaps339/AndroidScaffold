package com.gmlive.svgaplayer.memory

import com.gmlive.svgaplayer.recycle.VideoEntityRefCounter

internal class MemoryCacheService(
    private val referenceCounter: VideoEntityRefCounter,
    private val strongMemoryCache: StrongMemoryCache,
    private val weakMemoryCache: WeakMemoryCache
) {
    operator fun get(key: MemoryCache.Key?): RealMemoryCache.Value? {
        key ?: return null
        val value = strongMemoryCache.get(key) ?: weakMemoryCache.get(key)
        if (value != null) referenceCounter.increment(value.videoEntity)
        return value
    }
}
