package com.gmlive.svgaplayer

import android.content.Context
import com.gmlive.svgaplayer.util.*
import com.gmlive.svgaplayer.util.Utils
import okhttp3.Cache
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.io.File

/**
 * 默认 [SVGALoaderFactory]
 * @Author svenj
 * @Date 2020/11/27
 * @Email svenjzm@gmail.com
 */
class DefaultSVGALoaderFactory(private val context: Context) : SVGALoaderFactory {
    override fun newSvgaLoader(): SvgaLoader {
        return SvgaLoader.Builder(context)
                .availableMemoryPercentage(0.5)
                .okHttpClient {
                    // Create a disk cache with "unlimited" size. Don't do this in production.
                    // To create the an optimized Coil disk cache, use CoilUtils.createDefaultCache(context).
                    val cacheDirectory = Utils.getDefaultCacheDirectory(context)
                    val cache = Cache(cacheDirectory, Long.MAX_VALUE)

                    // Rewrite the Cache-Control header to cache all responses for a year.
                    val cacheControlInterceptor = ResponseHeaderInterceptor("Cache-Control", "max-age=31536000,public")

                    // Don't limit concurrent network requests by host.
                    val dispatcher = Dispatcher().apply { maxRequestsPerHost = maxRequests }

                    // Lazily create the OkHttpClient that is used for network operations.
                    OkHttpClient.Builder()
                            .cache(cache)
                            .dispatcher(dispatcher)
                            .ignoreVerify()
                            .setupSSLFactory()
                            .forceTls12() // The Unsplash API requires TLS 1.2, which isn't enabled by default before API 21.
                            .addNetworkInterceptor(cacheControlInterceptor)
                            .build()
                }
                .build()
    }
}