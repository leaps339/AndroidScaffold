@file:Suppress("MemberVisibilityCanBePrivate")

package com.gmlive.svgaplayer.network

import okhttp3.Response

class HttpException(val response: Response) : RuntimeException("HTTP ${response.code()}: ${response.message()}")
