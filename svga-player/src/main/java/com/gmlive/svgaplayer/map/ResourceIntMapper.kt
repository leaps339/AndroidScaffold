package com.gmlive.svgaplayer.map

import android.content.ContentResolver.SCHEME_ANDROID_RESOURCE
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.RawRes
import androidx.core.net.toUri
import com.gmlive.svgaplayer.decode.Options

class ResourceIntMapper : Mapper<Int, Uri> {

    override fun map(@RawRes data: Int, options: Options): Uri? {
        if (!isApplicable(data, options.context)) return null
        return "$SCHEME_ANDROID_RESOURCE://${options.context.packageName}/$data".toUri()
    }

    private fun isApplicable(@RawRes data: Int, context: Context): Boolean {
        return try {
            context.resources.getResourceEntryName(data) != null
        } catch (_: Resources.NotFoundException) {
            false
        }
    }
}