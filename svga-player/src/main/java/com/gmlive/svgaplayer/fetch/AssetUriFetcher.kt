package com.gmlive.svgaplayer.fetch

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.gmlive.svgaplayer.decode.DataSource
import com.gmlive.svgaplayer.decode.Options
import com.gmlive.svgaplayer.size.Size
import com.gmlive.svgaplayer.util.firstPathSegment
import com.gmlive.svgaplayer.util.getMimeTypeFromUrl
import com.gmlive.svgaplayer.util.md5Key
import okio.buffer
import okio.source

/**

 * @Author svenj
 * @Date 2020/11/25
 * @Email svenjzm@gmail.com
 */
class AssetUriFetcher(private val context: Context) : Fetcher<Uri> {
    companion object {
        const val ASSET_FILE_PATH_ROOT = "android_asset"
    }

    override fun handles(data: Uri): Boolean {
        return data.scheme == ContentResolver.SCHEME_FILE && data.firstPathSegment == ASSET_FILE_PATH_ROOT
    }

    override fun key(data: Uri) = data.toString().md5Key()

    override suspend fun fetch(data: Uri, size: Size, options: Options): FetchResult {
        val path = data.pathSegments.drop(1).joinToString("/")

        return SourceResult(
            source = context.assets.open(path).source().buffer(),
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromUrl(path),
            dataSource = DataSource.DISK
        )
    }
}