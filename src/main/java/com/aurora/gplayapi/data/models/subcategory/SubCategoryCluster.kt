package com.aurora.gplayapi.data.models.subcategory

import com.aurora.gplayapi.data.models.App

class SubCategoryCluster {
    var title: String = String()
    var browseUrl: String = String()
    var nextPageUrl: String = String()
    var appList: List<App> = ArrayList()
}