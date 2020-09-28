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

package com.aurora.gplayapi.data.providers

import com.aurora.gplayapi.data.models.AuthData
import java.util.*

object ParamProvider {

    fun getDefaultAuthParams(builder: AuthData): Map<String, String> {
        val params: MutableMap<String, String> = HashMap()
        if (builder.gsfId.isNotBlank())
            params["androidId"] = builder.gsfId
        params["sdk_version"] = builder.deviceInfoProvider!!.sdkVersion.toString()
        params["Email"] = builder.email
        params["google_play_services_version"] = builder.deviceInfoProvider!!.playServicesVersion.toString()
        params["device_country"] = builder.locale.country.toLowerCase()
        params["lang"] = builder.locale.language.toLowerCase()
        params["callerSig"] = "38918a453d07199354f8b19af05ec6562ced5788"
        return params
    }

    fun getAuthParams(aasToken: String): Map<String, String> {
        val params: MutableMap<String, String> = HashMap()
        params["app"] = "com.android.vending"
        params["client_sig"] = "38918a453d07199354f8b19af05ec6562ced5788"
        params["callerPkg"] = "com.google.android.gms"
        params["Token"] = aasToken
        params["oauth2_foreground"] = "1"
        params["token_request_options"] = "CAA4AVAB"
        params["check_email"] = "1"
        params["system_partition"] = "1"
        return params
    }
}