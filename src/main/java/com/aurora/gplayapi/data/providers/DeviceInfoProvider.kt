package com.aurora.gplayapi.data.providers

import com.aurora.gplayapi.AndroidCheckinRequest
import com.aurora.gplayapi.DeviceConfigurationProto

interface DeviceInfoProvider {
    fun generateAndroidCheckInRequest(): AndroidCheckinRequest?
    val deviceConfigurationProto: DeviceConfigurationProto?
    val userAgentString: String
    val authUserAgentString: String
    val mccMnc: String
    val sdkVersion: Int
    val playServicesVersion: Int
}