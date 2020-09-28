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

import java.io.FileNotFoundException
import java.util.*

object DeviceManager {
    fun loadProperties(deviceName: String?): Properties? {
        return try {
            val properties = Properties()
            val inputStream = javaClass
                    .classLoader
                    .getResourceAsStream(deviceName)
            if (inputStream != null) {
                properties.load(inputStream)
            } else {
                throw FileNotFoundException("Device config file not found")
            }
            properties
        } catch (e: Exception) {
            null
        }
    }
}