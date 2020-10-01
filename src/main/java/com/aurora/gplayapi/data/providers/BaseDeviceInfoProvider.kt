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

import com.aurora.gplayapi.AndroidCheckinRequest
import com.aurora.gplayapi.DeviceConfigurationProto

abstract class BaseDeviceInfoProvider {
    abstract fun generateAndroidCheckInRequest(): AndroidCheckinRequest?
    abstract val deviceConfigurationProto: DeviceConfigurationProto?
    abstract val userAgentString: String
    abstract val authUserAgentString: String
    abstract val mccMnc: String
    abstract val sdkVersion: Int
    abstract val playServicesVersion: Int
}
