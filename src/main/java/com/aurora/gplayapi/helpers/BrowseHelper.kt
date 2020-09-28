package com.aurora.gplayapi.helpers

import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.ResponseWrapper
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.providers.HeaderProvider.getDefaultHeaders
import com.aurora.gplayapi.network.HttpClient
import java.io.IOException
import java.util.*

class BrowseHelper(authData: AuthData) : BaseHelper(authData) {
    @Throws(IOException::class)
    fun getAllCategoriesList(type: Type) {
        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        val params: MutableMap<String, String> = HashMap()
        params["c"] = "3"
        params["cat"] = if (type == Type.GAME) "GAME" else "APPLICATION"
        val responseBody = HttpClient[GooglePlayApi.TOP_CHART_URL, headers, params]
        val responseWrapper = ResponseWrapper.parseFrom(responseBody!!.bytes())
        val payload = responseWrapper.payload
    }

    enum class Type {
        APPLICATION, GAME
    }
}