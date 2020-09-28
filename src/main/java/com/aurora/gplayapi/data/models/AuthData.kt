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

import com.aurora.gplayapi.data.providers.DeviceInfoProvider
import java.util.*

data class AuthData(val email: String,
                    val aasToken: String) {
    var authToken: String = String()
    var gsfId: String = String()
    var tokenDispenserUrl: String = String()
    var ac2dmToken: String = String()
    var androidCheckInToken: String = String()
    var deviceCheckInConsistencyToken: String = String()
    var deviceConfigToken: String = String()
    var experimentsConfigToken: String = String()
    var gcmToken: String = String()
    var oAuthLoginToken: String = String()
    var dfeCookie: String = String()
    var locale: Locale = Locale.getDefault()
    var deviceInfoProvider: DeviceInfoProvider? = null
}