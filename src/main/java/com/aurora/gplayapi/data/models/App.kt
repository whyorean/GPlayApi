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

package com.aurora.gplayapi.data.models

import com.aurora.gplayapi.Constants.Restriction
import com.aurora.gplayapi.Features
import com.aurora.gplayapi.FileMetadata

class App {
    var screenshotUrls: MutableList<String> = ArrayList()
    var permissions: MutableList<String> = ArrayList()
    var offerDetails: MutableMap<String, String> = HashMap()
    var relatedLinks: MutableMap<String, String> = HashMap()
    var dependencies: MutableSet<String> = HashSet()
    var categoryIconUrl: String? = null
    var categoryId: String? = null
    var categoryName: String? = null
    var changes: String? = null
    var description: String? = null
    var developerName: String? = null
    var developerEmail: String? = null
    var developerAddress: String? = null
    var developerWebsite: String? = null
    var displayName: String? = null
    var downloadString: String? = null
    var footerHtml: String? = null
    var iconUrl: String? = null
    var pageBackgroungUrl: String? = null
    var instantAppLink: String? = null
    var labeledRating: String? = null
    var packageName: String? = null
    var price: String? = null
    var shortDescription: String? = null
    var testingProgramEmail: String? = null
    var updated: String? = null
    var versionName: String? = null
    var videoUrl: String? = null
    var containsAds = false
    var earlyAccess = false
    var inPlayStore = false
    var isFree = false
    var isInstalled = false
    var system = false
    var testingProgramAvailable = false
    var testingProgramOptedIn = false
    var offerType = 0
    var versionCode = 0
    var installs: String = String()
    var size: Long = 0
    var rating: Rating? = null
    var restriction: Restriction? = null

    @Transient
    var userReview: Review? = null

    @Transient
    var features: Features? = null

    @Transient
    var fileMetadataList: List<FileMetadata> = ArrayList()
    var fileList: List<File> = ArrayList()
}