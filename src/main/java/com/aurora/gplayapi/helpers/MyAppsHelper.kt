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

package com.aurora.gplayapi.helpers

import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.ListResponse
import com.aurora.gplayapi.SingletonHolder
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.providers.HeaderProvider.getDefaultHeaders
import com.aurora.gplayapi.network.HttpClient
import java.util.*

class MyAppsHelper(authData: AuthData) : BaseHelper(authData) {

    companion object : SingletonHolder<MyAppsHelper, AuthData>(::MyAppsHelper)

    @get:Throws(Exception::class)
    val myApps: ListResponse?
        get() {
            val headers: Map<String, String> = getDefaultHeaders(authData)
            val params: Map<String, String> = HashMap()
            val responseBody = HttpClient[GooglePlayApi.URL_MY_APPS, headers, params]
            return getListResponseFromBytes(responseBody!!.bytes())
        }
}