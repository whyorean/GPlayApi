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
import com.aurora.gplayapi.data.builders.AppBuilder
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.providers.HeaderProvider.getDefaultHeaders
import com.aurora.gplayapi.network.HttpClient
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.util.*

class AppDetailsHelper(authData: AuthData) : BaseHelper(authData) {

    @Throws(Exception::class)
    fun getAppByPackageName(packageName: String): App? {
        val headers: Map<String, String> = getDefaultHeaders(authData)
        val params: MutableMap<String, String> = HashMap()
        params["doc"] = packageName

        val responseBody = HttpClient[GooglePlayApi.URL_DETAILS, headers, params]

        return if (responseBody != null) {
            val detailsResponse = getDetailsResponseFromBytes(responseBody.bytes())
            AppBuilder.build(detailsResponse!!.item)
        } else null
    }

    @Throws(Exception::class)
    fun getAppByPackageName(packageList: List<String>): List<App> {
        val appList: MutableList<App> = ArrayList()
        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        val request = getBulkDetailsBytes(packageList)

        if (!headers.containsKey("Content-Type")) {
            headers["Content-Type"] = "application/x-protobuf"
        }

        val requestBuilder = Request.Builder()
                .url(GooglePlayApi.URL_BULK_DETAILS)
                .post(RequestBody.create(MediaType.parse("application/x-protobuf"), request))


        val responseBody = requestBuilder.build().body()?.let {
            HttpClient.post(GooglePlayApi.URL_BULK_DETAILS, headers, it)
        }

        val payload = getPayLoadFromBytes(responseBody?.bytes())

        if (payload != null && payload.hasBulkDetailsResponse()) {
            val bulkDetailsResponse = payload.bulkDetailsResponse
            for (entry in bulkDetailsResponse.entryList) {
                appList.add(AppBuilder.build(entry.item))
            }
        }
        return appList
    }
}