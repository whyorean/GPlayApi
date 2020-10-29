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

import com.aurora.gplayapi.*
import java.util.*

class DeviceInfoProvider(var properties: Properties, var localeString: String) : BaseDeviceInfoProvider() {

    private var timeToReport = System.currentTimeMillis() / 1000

    init {
        ensureCompatibility(properties)
    }

    val isValid: Boolean = properties.keys.containsAll(listOf(*requiredFields))
    override val sdkVersion: Int = properties.getProperty("Build.VERSION.SDK_INT").toInt()
    override val playServicesVersion: Int = properties.getProperty("GSF.version").toInt()
    override val mccMnc: String = properties.getProperty("SimOperator")
    override val authUserAgentString: String = "GoogleAuth/1.4 (" + properties.getProperty("Build.DEVICE") + " " + properties.getProperty("Build.ID") + ")"

    override val userAgentString: String
        get() {
            val platforms = getList("Platforms").joinToString(separator = ";")

            val params = mutableListOf<String>()
            params.add("api=${3}")
            params.add("versionCode=${properties.getProperty("Vending.version")}")
            params.add("sdk=${properties.getProperty("Build.VERSION.SDK_INT")}")
            params.add("device=${properties.getProperty("Build.DEVICE")}")
            params.add("hardware=${properties.getProperty("Build.HARDWARE")}")
            params.add("product=${properties.getProperty("Build.PRODUCT")}")
            params.add("platformVersionRelease=${properties.getProperty("Build.VERSION.RELEASE")}")
            params.add("model=${properties.getProperty("Build.MODEL")}")
            params.add("buildId=${properties.getProperty("Build.ID")}")
            params.add("isWideScreen=${0}")
            params.add("supportedAbis=${platforms}")

            return "Android-Finsky/${properties.getProperty("Vending.versionString")} (${params.joinToString(separator = ",")})"
        }

    override fun generateAndroidCheckInRequest(): AndroidCheckinRequest? {
        return AndroidCheckinRequest.newBuilder()
                .setId(0)
                .setCheckin(
                        AndroidCheckinProto.newBuilder()
                                .setBuild(
                                        AndroidBuildProto.newBuilder()
                                                .setId(properties.getProperty("Build.FINGERPRINT"))
                                                .setProduct(properties.getProperty("Build.HARDWARE"))
                                                .setCarrier(properties.getProperty("Build.BRAND"))
                                                .setRadio(properties.getProperty("Build.RADIO"))
                                                .setBootloader(properties.getProperty("Build.BOOTLOADER"))
                                                .setDevice(properties.getProperty("Build.DEVICE"))
                                                .setSdkVersion(getInt("Build.VERSION.SDK_INT"))
                                                .setModel(properties.getProperty("Build.MODEL"))
                                                .setManufacturer(properties.getProperty("Build.MANUFACTURER"))
                                                .setBuildProduct(properties.getProperty("Build.PRODUCT"))
                                                .setClient(properties.getProperty("Client"))
                                                .setOtaInstalled(java.lang.Boolean.getBoolean(properties.getProperty("OtaInstalled")))
                                                .setTimestamp(timeToReport)
                                                .setGoogleServices(getInt("GSF.version"))
                                )
                                .setLastCheckinMsec(0)
                                .setCellOperator(properties.getProperty("CellOperator"))
                                .setSimOperator(properties.getProperty("SimOperator"))
                                .setRoaming(properties.getProperty("Roaming"))
                                .setUserNumber(0)
                )
                .setLocale(localeString)
                .setTimeZone(properties.getProperty("TimeZone"))
                .setVersion(3)
                .setDeviceConfiguration(deviceConfigurationProto)
                .setFragment(0)
                .build()
    }

    override val deviceConfigurationProto: DeviceConfigurationProto
        get() = DeviceConfigurationProto.newBuilder()
                .setTouchScreen(getInt("TouchScreen"))
                .setKeyboard(getInt("Keyboard"))
                .setNavigation(getInt("Navigation"))
                .setScreenLayout(getInt("ScreenLayout"))
                .setHasHardKeyboard(properties.getProperty("HasHardKeyboard").toBoolean())
                .setHasFiveWayNavigation(properties.getProperty("HasFiveWayNavigation").toBoolean())
                .setLowRamDevice(properties.getProperty("LowRamDevice", "0").toInt())
                .setMaxNumOfCPUCores(properties.getProperty("MaxNumOfCPUCores", "8").toInt())
                .setTotalMemoryBytes(properties.getProperty("TotalMemoryBytes", "8589935000").toLong())
                .setDeviceClass(0)
                .setScreenDensity(getInt("Screen.Density"))
                .setScreenWidth(getInt("Screen.Width"))
                .setScreenHeight(getInt("Screen.Height"))
                .addAllNativePlatform(getList("Platforms"))
                .addAllSystemSharedLibrary(getList("SharedLibraries"))
                .addAllSystemAvailableFeature(getList("Features"))
                .addAllSystemSupportedLocale(getList("Locales"))
                .setGlEsVersion(getInt("GL.Version"))
                .addAllGlExtension(getList("GL.Extensions"))
                .addAllDeviceFeature(getDeviceFeatures())
                .build()

    private fun getInt(key: String): Int {
        return try {
            properties.getProperty(key).toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    private fun getList(key: String): List<String> {
        return listOf(*properties.getProperty(key).split(",".toRegex()).toTypedArray())
    }

    private fun getDeviceFeatures(): List<DeviceFeature> {
        return getList("Features").map {
            DeviceFeature.newBuilder()
                    .setName(it)
                    .setValue(0)
                    .build()
        }
    }

    private fun ensureCompatibility(properties: Properties) {
        if (!properties.containsKey("Vending.versionString") && properties.containsKey("Vending.version")) {
            var vendingVersionString = "7.1.15"
            if (properties.getProperty("Vending.version").length > 6) {
                vendingVersionString = StringBuilder(properties.getProperty("Vending.version").substring(2, 6)).insert(2, ".").insert(1, ".").toString()
            }
            properties["Vending.versionString"] = vendingVersionString
        }
        if (properties.containsKey("Build.FINGERPRINT") && (!properties.containsKey("Build.ID")
                        || !properties.containsKey("Build.VERSION.RELEASE"))) {
            val fingerprint = properties.getProperty("Build.FINGERPRINT").split("/".toRegex()).toTypedArray()
            var buildId = ""
            var release = ""

            if (fingerprint.size > 5) {
                var releaseFound = false
                for (component in fingerprint) {
                    if (component.contains(":")) {
                        release = component.split(":".toRegex()).toTypedArray()[1]
                        releaseFound = true
                        continue
                    }
                    if (releaseFound) {
                        buildId = component
                        break
                    }
                }
            }
            if (!properties.containsKey("Build.ID")) {
                properties["Build.ID"] = buildId
            }
            if (!properties.containsKey("Build.VERSION.RELEASE")) {
                properties["Build.VERSION.RELEASE"] = release
            }
        }
    }

    companion object {
        private val requiredFields = arrayOf(
                "UserReadableName",
                "Build.HARDWARE",
                "Build.RADIO",
                "Build.BOOTLOADER",
                "Build.FINGERPRINT",
                "Build.BRAND",
                "Build.DEVICE",
                "Build.VERSION.SDK_INT",
                "Build.MODEL",
                "Build.MANUFACTURER",
                "Build.PRODUCT",
                "TouchScreen",
                "Keyboard",
                "Navigation",
                "ScreenLayout",
                "HasHardKeyboard",
                "HasFiveWayNavigation",
                "GL.Version",
                "GSF.version",
                "Vending.version",
                "Screen.Density",
                "Screen.Width",
                "Screen.Height",
                "Platforms",
                "SharedLibraries",
                "Features",
                "Locales",
                "CellOperator",
                "SimOperator",
                "Roaming",
                "Client",
                "TimeZone",
                "GL.Extensions"
        )
    }
}