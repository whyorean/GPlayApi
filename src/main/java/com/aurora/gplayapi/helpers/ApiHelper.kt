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

import com.aurora.gplayapi.SingletonHolder
import com.aurora.gplayapi.data.models.AuthData

class ApiHelper(authData: AuthData) {

    companion object : SingletonHolder<ApiHelper, AuthData>(::ApiHelper)

    val appDetailsHelper = AppDetailsHelper(authData)
    val browseHelper = BrowseHelper(authData)
    val categoryHelper = CategoryHelper(authData)
    val myAppsHelper = MyAppsHelper(authData)
    val purchaseHelper = PurchaseHelper(authData)
    val reviewsHelper = ReviewsHelper(authData)
    val searchHelper = SearchHelper(authData)
    val streamHelper = StreamHelper(authData)
    val topChartsHelper = TopChartsHelper(authData)
}