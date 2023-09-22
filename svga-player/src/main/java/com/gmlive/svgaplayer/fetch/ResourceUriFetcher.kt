package com.gmlive.svgaplayer.fetch

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.TypedValue
import android.webkit.MimeTypeMap
import com.gmlive.svgaplayer.decode.DataSource
import com.gmlive.svgaplayer.decode.Options
import com.gmlive.svgaplayer.size.Size
import com.gmlive.svgaplayer.util.getMimeTypeFromUrl
import com.gmlive.svgaplayer.util.md5Key
import com.gmlive.svgaplayer.util.nightMode
import okio.buffer
import okio.source

internal class ResourceUriFetcher(private val context: Context) : Fetcher<Uri> {

    override fun handles(data: Uri) = data.scheme == ContentResolver.SCHEME_ANDROID_RESOURCE

    override fun key(data: Uri) = "$data-${context.resources.configuration.nightMode}".md5Key()

    override suspend fun fetch(
        data: Uri,
        size: Size,
        options: Options
    ): FetchResult {
        // Expected format: android.resource://example.package.name/12345678
        val packageName = data.authority?.takeIf { it.isNotBlank() } ?: throwInvalidUriException(data)
        val resId = data.pathSegments.lastOrNull()?.toIntOrNull() ?: throwInvalidUriException(data)

        val context = options.context
        val resources = context.packageManager.getResourcesForApplication(packageName)
        val path = TypedValue().apply { resources.getValue(resId, this, true) }.string
        val entryName = path.substring(path.lastIndexOf('/'))
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromUrl(entryName)

        return SourceResult(
                source = resources.openRawResource(resId).source().buffer(),
                mimeType = mimeType,
                dataSource = DataSource.MEMORY
            )
    }

    private fun throwInvalidUriException(data: Uri): Nothing {
        throw IllegalStateException("Invalid ${ContentResolver.SCHEME_ANDROID_RESOURCE} URI: $data")
    }

}
