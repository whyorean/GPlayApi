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