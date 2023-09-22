package com.gmlive.svgaplayer.fetch

import android.webkit.MimeTypeMap
import com.gmlive.svgaplayer.decode.DataSource
import com.gmlive.svgaplayer.decode.Options
import com.gmlive.svgaplayer.size.Size
import com.gmlive.svgaplayer.util.md5Key
import okio.buffer
import okio.source
import java.io.File

/**

 * @Author svenj
 * @Date 2020/11/25
 * @Email svenjzm@gmail.com
 */
class FileFetcher(private val addLastModifiedToFileCacheKey: Boolean) : Fetcher<File> {

    override fun key(data: File): String {
        val keyString = if (addLastModifiedToFileCacheKey) "${data.path}:${data.lastModified()}" else data.path

        return keyString.md5Key()
    }

    override suspend fun fetch(data: File, size: Size, options: Options): FetchResult {
        return SourceResult(
            source = data.source().buffer(),
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(data.extension),
            dataSource = DataSource.DISK
        )
    }
}