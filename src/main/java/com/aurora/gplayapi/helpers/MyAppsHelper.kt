package com.aurora.gplayapi.helpers

import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.ListResponse
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.providers.HeaderProvider.getDefaultHeaders
import com.aurora.gplayapi.network.HttpClient
import java.util.*

class MyAppsHelper(authData: AuthData) : BaseHelper(authData) {
    @get:Throws(Exception::class)
    val myApps: ListResponse?
        get() {
            val headers: Map<String, String> = getDefaultHeaders(authData)
            val params: Map<String, String> = HashMap()
            val responseBody = HttpClient[GooglePlayApi.URL_MY_APPS, headers, params]
            return getListResponseFromBytes(responseBody!!.bytes())
        }
}