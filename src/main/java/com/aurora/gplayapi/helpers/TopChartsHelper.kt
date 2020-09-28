package com.aurora.gplayapi.helpers

import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.StreamCluster
import com.aurora.gplayapi.data.providers.HeaderProvider.getDefaultHeaders
import com.aurora.gplayapi.network.HttpClient
import java.util.*

class TopChartsHelper(authData: AuthData) : BaseHelper(authData) {

    @Throws(Exception::class)
    fun getTopChartStream(type: Type, chart: Chart): StreamCluster? {
        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        val params: MutableMap<String, String> = HashMap()
        params["c"] = "3"
        params["stcid"] = chart.value
        params["scat"] = type.value

        val responseBody = HttpClient[GooglePlayApi.TOP_CHART_URL, headers, params]
        val listResponse = getListResponseFromBytes(responseBody?.bytes())
        return getStreamCluster(listResponse)
    }

    enum class Chart(var value: String) {
        TOP_SELLING_FREE("apps_topselling_free"),
        TOP_SELLING_PAID("apps_topselling_paid"),
        TOP_GROSSING("apps_topgrossing"),
        MOVERS_SHAKERS("apps_movers_shakers");
    }

    enum class Type(var value: String) {
        GAME("GAME"),
        APPLICATION("APPLICATION");
    }
}