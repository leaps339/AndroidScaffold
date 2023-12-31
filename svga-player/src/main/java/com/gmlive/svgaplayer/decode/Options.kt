package com.gmlive.svgaplayer.decode

import android.content.Context
import com.gmlive.svgaplayer.request.CachePolicy
import com.gmlive.svgaplayer.request.Parameters
import com.gmlive.svgaplayer.util.EMPTY_HEADERS
import okhttp3.Headers

/**

 * @Author svenj
 * @Date 2020/11/25
 * @Email svenjzm@gmail.com
 */
class Options(
        val context: Context,
        val allowInexactSize: Boolean = false,
        val headers: Headers = EMPTY_HEADERS,
        val parameters: Parameters = Parameters.EMPTY,
        val memoryCachePolicy: CachePolicy = CachePolicy.ENABLED,
        val diskCachePolicy: CachePolicy = CachePolicy.ENABLED,
        val networkCachePolicy: CachePolicy = CachePolicy.ENABLED
) {
    fun copy(
        context: Context = this.context,
        allowInexactSize: Boolean = this.allowInexactSize,
        headers: Headers = this.headers,
        parameters: Parameters = this.parameters,
        memoryCachePolicy: CachePolicy = this.memoryCachePolicy,
        diskCachePolicy: CachePolicy = this.diskCachePolicy,
        networkCachePolicy: CachePolicy = this.networkCachePolicy
    ) = Options(
        context, allowInexactSize, headers, parameters, memoryCachePolicy, diskCachePolicy,
        networkCachePolicy
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Options &&
                context == other.context &&
                allowInexactSize == other.allowInexactSize &&
                headers == other.headers &&
                parameters == other.parameters &&
                memoryCachePolicy == other.memoryCachePolicy &&
                diskCachePolicy == other.diskCachePolicy &&
                networkCachePolicy == other.networkCachePolicy
    }

    override fun hashCode(): Int {
        var result = context.hashCode()
        result = 31 * result + allowInexactSize.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + parameters.hashCode()
        result = 31 * result + memoryCachePolicy.hashCode()
        result = 31 * result + diskCachePolicy.hashCode()
        result = 31 * result + networkCachePolicy.hashCode()
        return result
    }

    override fun toString(): String {
        return "Options(context=$context, allowInexactSize=$allowInexactSize, headers=$headers, " +
                "parameters=$parameters, memoryCachePolicy=$memoryCachePolicy, " +
                "diskCachePolicy=$diskCachePolicy, networkCachePolicy=$networkCachePolicy)"
    }
}