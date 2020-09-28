package com.aurora.gplayapi.helpers

import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.ListResponse
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.StreamCluster
import com.aurora.gplayapi.data.providers.HeaderProvider.getDefaultHeaders
import com.aurora.gplayapi.network.HttpClient
import java.util.*

class StreamHelper(authData: AuthData) : BaseHelper(authData) {

    @Throws(Exception::class)
    fun getList(type: Type, category: Category?): ListResponse? {
        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        val params: MutableMap<String, String> = HashMap()
        params["c"] = "3"

        if (type == Type.EARLY_ACCESS) {
            params["ct"] = "1"
        } else {
            params["cat"] = category!!.value
        }
        val responseBody = HttpClient[GooglePlayApi.URL_FDFE + "/" + type.value, headers, params]
        return getListResponseFromBytes(responseBody!!.bytes())
    }

    @Throws(Exception::class)
    fun getMyAppsStream(type: StreamType): StreamCluster? {
        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        val params: MutableMap<String, String> = HashMap()
        params["n"] = "15"
        params["tab"] = type.value
        val responseBody = HttpClient[GooglePlayApi.URL_FDFE + "/myAppsStream", headers, params]
        val listResponse = getListResponseFromBytes(responseBody!!.bytes())
        return getStreamCluster(listResponse)
    }

    enum class Category(var value: String) {
        APPLICATION("APPLICATION"),
        GAME("GAME");
    }

    enum class Type(var value: String) {
        EARLY_ACCESS("appsEarlyAccessStream"),
        EDITOR_CHOICE("getAppsEditorsChoiceStream"),
        HOME("getHomeStream"),
        MY_APPS_LIBRARY("myAppsStream?tab=LIBRARY"),
        PREMIUM_GAMES("getAppsPremiumGameStream"),
        SUB_NAV("subnavHome"),
        TOP_CHART("topChartsStream");
    }

    enum class StreamType(var value: String) {
        MY_APPS_INSTALLED("INSTALLED"),
        MY_APPS_LIBRARY("LIBRARY"),
        MY_APPS_UPDATES("UPDATES");
    }
}