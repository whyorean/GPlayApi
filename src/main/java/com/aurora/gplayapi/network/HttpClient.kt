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
import com.aurora.gplayapi.exceptions.AuthException
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.create
import java.io.IOException

object HttpClient : HttpClientImpl {

    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(GooglePlayApi.URL_BASE)
            .build()

    private val HTTP_SERVICE: HttpService

    init {
        HTTP_SERVICE = retrofit.create()
    }

    @Throws(IOException::class)
    override fun post(url: String, headers: Map<String, String>, requestBody: RequestBody): ResponseBody? {
        val call = HTTP_SERVICE.post(url, headers, requestBody)
        val response = call.execute()
        if (response.body() == null) {
            throw AuthException(response.errorBody()?.string())
        }
        return response.body()
    }

    @Throws(IOException::class)
    override fun post(url: String, headers: Map<String, String>, params: Map<String, String>): ResponseBody? {
        val call = HTTP_SERVICE.post(url, headers, params)
        val response = call.execute()
        if (response.body() == null) {
            throw AuthException(response.errorBody()?.string())
        }
        return response.body()
    }

    @Throws(IOException::class)
    override fun get(url: String, headers: Map<String, String>): ResponseBody? {
        val call = HTTP_SERVICE[url, headers]
        val response = call.execute()
        return response.body()
    }

    @Throws(IOException::class)
    override fun get(url: String, headers: Map<String, String>, params: Map<String, String>): ResponseBody? {
        val call = HTTP_SERVICE[url, headers, params]
        val response = call.execute()
        return response.body()
    }

    @Throws(IOException::class)
    override fun getX(url: String, headers: Map<String, String>, paramString: String): ResponseBody? {
        val call = HTTP_SERVICE[url + paramString, headers]
        val response = call.execute()
        return response.body()
    }
}