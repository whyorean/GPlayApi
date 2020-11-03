/*
 *     GPlayApi
 *     Copyright (C) 2020  Aurora OSS
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 */

package com.aurora.gplayapi.network

import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.data.models.PlayResponse
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object HttpClient {

    private val okHttpClient: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .cache(Cache(File("okhttp_cache"), 50 * 1024 * 1024L))
            .retryOnConnectionFailure(true)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()

    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(GooglePlayApi.URL_BASE)
            .client(okHttpClient)
            .build()

    private val HTTP_SERVICE: HttpService

    init {
        HTTP_SERVICE = retrofit.create()
    }

    @Throws(IOException::class)
    fun post(url: String, headers: Map<String, String>, requestBody: RequestBody): PlayResponse {
        val call = HTTP_SERVICE.post(url, headers, requestBody)
        return buildPlayResponse(call.execute())
    }

    @Throws(IOException::class)
    fun post(url: String, headers: Map<String, String>, params: Map<String, String>): PlayResponse {
        val call = HTTP_SERVICE.post(url, headers, params)
        return buildPlayResponse(call.execute())
    }

    @Throws(IOException::class)
    fun get(url: String, headers: Map<String, String>): PlayResponse {
        val call = HTTP_SERVICE.get(url, headers)
        return buildPlayResponse(call.execute())
    }

    @Throws(IOException::class)
    fun get(url: String, headers: Map<String, String>, params: Map<String, String>): PlayResponse {
        val call = HTTP_SERVICE[url, headers, params]
        return buildPlayResponse(call.execute())
    }

    @Throws(IOException::class)
    fun getX(url: String, headers: Map<String, String>, paramString: String): PlayResponse {
        val call = HTTP_SERVICE[url + paramString, headers]
        return buildPlayResponse(call.execute())
    }

    @JvmStatic
    private fun buildPlayResponse(response: Response<ResponseBody>): PlayResponse {
        return PlayResponse().apply {
            if (response.body() != null)
                responseBytes = response.body()!!.bytes()
            if (response.errorBody() != null) {
                errorBytes = response.errorBody()!!.bytes()
                errorString = String(errorBytes)
            }
            isSuccessful = response.isSuccessful
            code = response.code()
        }.also {
            println(response.raw())
        }
    }
}