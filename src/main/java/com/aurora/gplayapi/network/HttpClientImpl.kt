package com.aurora.gplayapi.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.IOException

interface HttpClientImpl {
    @Throws(IOException::class)
    fun post(url: String, headers: Map<String, String>, requestBody: RequestBody): ResponseBody?

    @Throws(IOException::class)
    fun post(url: String, headers: Map<String, String>, params: Map<String, String>): ResponseBody?

    @Throws(IOException::class)
    operator fun get(url: String, headers: Map<String, String>): ResponseBody?

    @Throws(IOException::class)
    operator fun get(url: String, headers: Map<String, String>, params: Map<String, String>): ResponseBody?

    @Throws(IOException::class)
    fun getX(url: String, headers: Map<String, String>, paramString: String): ResponseBody?
}