package com.aurora.gplayapi.helpers

import com.aurora.gplayapi.SingletonHolder
import com.aurora.gplayapi.data.models.AuthData

class ApiHelper(authData: AuthData) {

    companion object : SingletonHolder<ApiHelper, AuthData>(::ApiHelper)

    val appDetailsHelper = AppDetailsHelper(authData)
        get
    val browseHelper = BrowseHelper(authData)
    val categoryHelper = CategoryHelper(authData)
    val myAppsHelper = MyAppsHelper(authData)
    val purchaseHelper = PurchaseHelper(authData)
    val reviewsHelper = ReviewsHelper(authData)
    val searchHelper = SearchHelper(authData)
    val streamHelper = StreamHelper(authData)
    val topChartsHelper = TopChartsHelper(authData)
}