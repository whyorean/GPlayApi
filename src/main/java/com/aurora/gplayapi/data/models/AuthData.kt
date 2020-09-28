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