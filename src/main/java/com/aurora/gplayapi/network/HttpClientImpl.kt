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