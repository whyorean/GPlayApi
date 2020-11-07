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
import com.aurora.gplayapi.SingletonHolder
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.sale.SaleBundle
import com.aurora.gplayapi.network.HttpClient
import com.google.gson.Gson
import java.io.IOException
import java.util.*

class AppSalesHelper private constructor(authData: AuthData) : BaseHelper(authData) {

    companion object : SingletonHolder<AppSalesHelper, AuthData>(::AppSalesHelper)

    @Throws(IOException::class)
    fun getAppsOnSale(page: Int, offer: Int = 100, type: String = ""): List<App> {
        val params: MutableMap<String, String> = mutableMapOf()
        params["page"] = page.toString()
        params["country"] = Locale.getDefault().country
        params["language"] = Locale.getDefault().language
        params["minreduc"] = offer.toString()

        /*params["typefilter"] = type Not sure of values */

        val playResponse = HttpClient.get(GooglePlayApi.SALES_URL, headers = mapOf(), params)
        val saleBundle = Gson().fromJson(String(playResponse.responseBytes), SaleBundle::class.java)

        return if (saleBundle.sales.isEmpty())
            listOf()
        else {
            val appDetailsHelper = AppDetailsHelper.with(authData)
            return appDetailsHelper.getAppByPackageName(packageList = saleBundle.sales.map { it.idandroid }.toList())
        }
    }
}