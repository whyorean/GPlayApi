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

package com.aurora.gplayapi

import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.providers.HeaderProvider.getAASTokenHeaders
import com.aurora.gplayapi.data.providers.HeaderProvider.getAuthHeaders
import com.aurora.gplayapi.data.providers.HeaderProvider.getDefaultHeaders
import com.aurora.gplayapi.data.providers.ParamProvider.getAuthParams
import com.aurora.gplayapi.data.providers.ParamProvider.getDefaultAuthParams
import com.aurora.gplayapi.exceptions.AuthException
import com.aurora.gplayapi.network.HttpClient
import com.aurora.gplayapi.utils.Util
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import java.math.BigInteger
import java.util.*

class GooglePlayApi(private val authData: AuthData) {

    @Throws(IOException::class)
    fun toc(): TocResponse {
        val playResponse = HttpClient.get(URL_TOC, getDefaultHeaders(authData))
        val tocResponse = ResponseWrapper.parseFrom(playResponse.responseBytes).payload.tocResponse
        if (tocResponse.tosContent.isNotBlank() && tocResponse.tosToken.isNotBlank()) {
            acceptTos(tocResponse.tosToken)
        }
        if (tocResponse.cookie.isNotBlank()) {
            authData.dfeCookie = tocResponse.cookie
        }
        return tocResponse
    }

    @Throws(IOException::class)
    private fun acceptTos(tosToken: String): AcceptTosResponse {
        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        val params: MutableMap<String, String> = HashMap()
        params["tost"] = tosToken
        params["toscme"] = "false"

        val playResponse = HttpClient.post(URL_TOS_ACCEPT, headers, params)
        return ResponseWrapper.parseFrom(playResponse.responseBytes)
                .payload
                .acceptTosResponse
    }

    @Throws(IOException::class)
    fun uploadDeviceConfig(): UploadDeviceConfigResponse {
        val request = UploadDeviceConfigRequest.newBuilder()
                .setDeviceConfiguration(authData.deviceInfoProvider!!.deviceConfigurationProto)
                .build()

        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        headers["X-DFE-Enabled-Experiments"] = "cl:billing.select_add_instrument_by_default"
        headers["X-DFE-Unsupported-Experiments"] = "nocache:billing.use_charging_poller,market_emails,buyer_currency,prod_baseline,checkin.set_asset_paid_app_field,shekel_test,content_ratings,buyer_currency_in_app,nocache:encrypted_apk,recent_changes"
        headers["X-DFE-SmallestScreenWidthDp"] = "320"
        headers["X-DFE-Filter-Level"] = "3"

        val requestBuilder = Request.Builder()
                .url(URL_UPLOAD_DEVICE_CONFIG)
                .post(RequestBody.create(MediaType.parse("application/x-protobuf"), request.toByteArray()))

        val playResponse = requestBuilder.build().body()?.let {
            HttpClient.post(URL_UPLOAD_DEVICE_CONFIG, headers, it)
        }

        val configResponse = ResponseWrapper.parseFrom(playResponse?.responseBytes)
                .payload
                .uploadDeviceConfigResponse

        if (configResponse.uploadDeviceConfigToken.isNotBlank()) {
            authData.deviceConfigToken = configResponse.uploadDeviceConfigToken
        }
        return configResponse
    }

    @Throws(IOException::class)
    fun generateGsfId(): String {
        val request = authData.deviceInfoProvider?.generateAndroidCheckInRequest()
        val checkInResponse = checkIn(request!!.toByteArray())
        val gsfId = BigInteger.valueOf(checkInResponse.androidId).toString(16)
        authData.gsfId = gsfId
        authData.deviceCheckInConsistencyToken = checkInResponse.deviceCheckinConsistencyToken
        return gsfId
    }

    @Throws(IOException::class)
    private fun checkIn(request: ByteArray): AndroidCheckinResponse {
        val headers: MutableMap<String, String> = getAuthHeaders(authData)
        headers["Content-Type"] = "application/x-protobuffer"
        headers["Host"] = "android.clients.google.com"
        val requestBuilder = Request.Builder()
                .url(URL_CHECK_IN)
                .post(RequestBody.create(MediaType.parse("application/x-protobuf"), request))
        val responseBody = requestBuilder.build().body()?.let {
            HttpClient.post(URL_CHECK_IN, headers, it)
        }
        return AndroidCheckinResponse.parseFrom(responseBody?.responseBytes)
    }

    @Throws(IOException::class)
    fun generateAASToken(email: String, oauthToken: String): String? {
        val params: MutableMap<String, String> = HashMap()
        params.putAll(getDefaultAuthParams(authData))
        params.putAll(getAASTokenHeaders(email, oauthToken))
        val headers: MutableMap<String, String> = getAuthHeaders(authData)
        headers["app"] = "com.android.vending"
        val playResponse = HttpClient.post(URL_AUTH, headers, params)
        val hashMap = Util.parseResponse(playResponse.responseBytes)
        return if (hashMap.containsKey("Token")) {
            hashMap["Token"]
        } else {
            throw AuthException("Authentication failed")
        }
    }

    @Throws(IOException::class)
    fun generateToken(aasToken: String, service: Service): String {
        val headers: MutableMap<String, String> = getAuthHeaders(authData)
        val params: MutableMap<String, String> = HashMap()
        params.putAll(getDefaultAuthParams(authData))
        params.putAll(getAuthParams(aasToken))

        when (service) {
            Service.AC2DM -> {
                params["service"] = "ac2dm"
                params.remove("app")
            }
            Service.ANDROID_CHECK_IN_SERVER -> {
                params["oauth2_foreground"] = "0"
                params["app"] = "com.google.android.gms"
                params["service"] = "AndroidCheckInServer"
            }
            Service.EXPERIMENTAL_CONFIG -> params["service"] = "oauth2:https://www.googleapis.com/auth/experimentsandconfigs"
            Service.NUMBERER -> {
                params["app"] = "com.google.android.gms"
                params["service"] = "oauth2:https://www.googleapis.com/auth/numberer"
            }
            Service.GCM -> {
                params["app"] = "com.google.android.gms"
                params["service"] = "oauth2:https://www.googleapis.com/auth/gcm"
            }
            Service.GOOGLE_PLAY -> {
                headers["app"] = "com.google.android.gms"
                params["service"] = "oauth2:https://www.googleapis.com/auth/googleplay"
            }
            Service.OAUTHLOGIN -> {
                params["oauth2_foreground"] = "0"
                params["app"] = "com.google.android.googlequicksearchbox"
                params["service"] = "oauth2:https://www.google.com/accounts/OAuthLogin"
                params["callerPkg"] = "com.google.android.googlequicksearchbox"
            }
            else -> {

            }
        }

        val playResponse = HttpClient.post(URL_AUTH, headers, params)
        val hashMap = Util.parseResponse(playResponse.responseBytes)

        return if (hashMap.containsKey("Auth")) {
            val token = hashMap["Auth"]
            authData.authToken = token!!
            token
        } else {
            throw AuthException("Authentication failed")
        }
    }

    enum class Service {
        AC2DM,
        ANDROID,
        ANDROID_CHECK_IN_SERVER,
        EXPERIMENTAL_CONFIG,
        GCM,
        GOOGLE_PLAY,
        NUMBERER,
        OAUTHLOGIN
    }

    companion object {
        const val URL_BASE = "https://android.clients.google.com"
        const val URL_FDFE = "$URL_BASE/fdfe"
        const val CATEGORIES_URL = "$URL_FDFE/categoriesList"
        const val CATEGORIES_URL_2 = "$URL_FDFE/allCategoriesList"
        const val DELIVERY_URL = "$URL_FDFE/delivery"
        const val PURCHASE_URL = "$URL_FDFE/purchase"
        const val TOP_CHART_URL = "$URL_FDFE/listTopChartItems"
        const val URL_AUTH = "$URL_BASE/auth"
        const val URL_BULK_DETAILS = "$URL_FDFE/bulkDetails"
        const val URL_BULK_PREFETCH = "$URL_FDFE/bulkPrefetch"
        const val URL_CHECK_IN = "$URL_BASE/checkin"
        const val URL_DETAILS = "$URL_FDFE/details"
        const val URL_MY_APPS = "$URL_FDFE/myApps"
        const val URL_REVIEW_ADD_EDIT = "$URL_FDFE/addReview"
        const val URL_REVIEW_USER = "$URL_FDFE/userReview"
        const val URL_REVIEWS = "$URL_FDFE/rev"
        const val URL_SEARCH = "$URL_FDFE/search"
        const val URL_SEARCH_SUGGEST = "$URL_FDFE/searchSuggest"
        const val URL_TOC = "$URL_FDFE/toc"
        const val URL_TOS_ACCEPT = "$URL_FDFE/acceptTos"
        const val URL_UPLOAD_DEVICE_CONFIG = "$URL_FDFE/uploadDeviceConfig"
    }
}