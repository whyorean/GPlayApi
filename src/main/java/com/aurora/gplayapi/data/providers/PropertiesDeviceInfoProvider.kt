package com.aurora.gplayapi.data.providers

import com.aurora.gplayapi.AndroidBuildProto
import com.aurora.gplayapi.AndroidCheckinProto
import com.aurora.gplayapi.AndroidCheckinRequest
import com.aurora.gplayapi.DeviceConfigurationProto
import java.util.*

class PropertiesDeviceInfoProvider : DeviceInfoProvider {
    private var properties: Properties? = null
    private var localeString: String? = null
    private var timeToReport = System.currentTimeMillis() / 1000

    fun setProperties(properties: Properties) {
        ensureCompatibility(properties)
        this.properties = properties
    }

    fun setLocaleString(localeString: String?) {
        this.localeString = localeString
    }

    fun setTimeToReport(timeToReport: Long) {
        this.timeToReport = timeToReport
    }

    val isValid: Boolean
        get() = properties!!.keys.containsAll(listOf(*requiredFields))
    override val sdkVersion: Int
        get() = properties!!.getProperty("Build.VERSION.SDK_INT").toInt()
    override val playServicesVersion: Int
        get() = properties!!.getProperty("GSF.version").toInt()
    override val mccMnc: String
        get() = properties!!.getProperty("SimOperator")
    override val authUserAgentString: String
        get() = "GoogleAuth/1.4 (" + properties!!.getProperty("Build.DEVICE") + " " + properties!!.getProperty("Build.ID") + ")"
    override val userAgentString: String
        get() {
            val platforms = StringBuilder()
            for (platform in getList("Platforms")) {
                platforms.append(";").append(platform)
            }
            return ("Android-Finsky/" + properties!!.getProperty("Vending.versionString") + " ("
                    + "api=3"
                    + ",versionCode=" + properties!!.getProperty("Vending.version")
                    + ",sdk=" + properties!!.getProperty("Build.VERSION.SDK_INT")
                    + ",device=" + properties!!.getProperty("Build.DEVICE")
                    + ",hardware=" + properties!!.getProperty("Build.HARDWARE")
                    + ",product=" + properties!!.getProperty("Build.PRODUCT")
                    + ",platformVersionRelease=" + properties!!.getProperty("Build.VERSION.RELEASE")
                    + ",model=" + properties!!.getProperty("Build.MODEL")
                    + ",buildId=" + properties!!.getProperty("Build.ID")
                    + ",isWideScreen=0"
                    + ",supportedAbis=" + platforms.toString().substring(1)
                    + ")")
        }

    override fun generateAndroidCheckInRequest(): AndroidCheckinRequest? {
        return AndroidCheckinRequest.newBuilder()
                .setId(0)
                .setCheckin(
                        AndroidCheckinProto.newBuilder()
                                .setBuild(
                                        AndroidBuildProto.newBuilder()
                                                .setId(properties!!.getProperty("Build.FINGERPRINT"))
                                                .setProduct(properties!!.getProperty("Build.HARDWARE"))
                                                .setCarrier(properties!!.getProperty("Build.BRAND"))
                                                .setRadio(properties!!.getProperty("Build.RADIO"))
                                                .setBootloader(properties!!.getProperty("Build.BOOTLOADER"))
                                                .setDevice(properties!!.getProperty("Build.DEVICE"))
                                                .setSdkVersion(getInt("Build.VERSION.SDK_INT"))
                                                .setModel(properties!!.getProperty("Build.MODEL"))
                                                .setManufacturer(properties!!.getProperty("Build.MANUFACTURER"))
                                                .setBuildProduct(properties!!.getProperty("Build.PRODUCT"))
                                                .setClient(properties!!.getProperty("Client"))
                                                .setOtaInstalled(java.lang.Boolean.getBoolean(properties!!.getProperty("OtaInstalled")))
                                                .setTimestamp(timeToReport)
                                                .setGoogleServices(getInt("GSF.version"))
                                )
                                .setLastCheckinMsec(0)
                                .setCellOperator(properties!!.getProperty("CellOperator"))
                                .setSimOperator(properties!!.getProperty("SimOperator"))
                                .setRoaming(properties!!.getProperty("Roaming"))
                                .setUserNumber(0)
                )
                .setLocale(localeString)
                .setTimeZone(properties!!.getProperty("TimeZone"))
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
                .setHasHardKeyboard(java.lang.Boolean.getBoolean(properties!!.getProperty("HasHardKeyboard")))
                .setHasFiveWayNavigation(java.lang.Boolean.getBoolean(properties!!.getProperty("HasFiveWayNavigation")))
                .setScreenDensity(getInt("Screen.Density"))
                .setScreenWidth(getInt("Screen.Width"))
                .setScreenHeight(getInt("Screen.Height"))
                .addAllNativePlatform(getList("Platforms"))
                .addAllSystemSharedLibrary(getList("SharedLibraries"))
                .addAllSystemAvailableFeature(getList("Features"))
                .addAllSystemSupportedLocale(getList("Locales"))
                .setGlEsVersion(getInt("GL.Version"))
                .addAllGlExtension(getList("GL.Extensions"))
                .build()

    private fun getInt(key: String): Int {
        return try {
            properties!!.getProperty(key).toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    private fun getList(key: String): List<String> {
        return listOf(*properties!!.getProperty(key).split(",".toRegex()).toTypedArray())
    }

    private fun ensureCompatibility(properties: Properties) {
        if (!properties.containsKey("Vending.versionString") && properties.containsKey("Vending.version")) {
            var vendingVersionString = "7.1.15"
            if (properties.getProperty("Vending.version").length > 6) {
                vendingVersionString = StringBuilder(properties.getProperty("Vending.version").substring(2, 6)).insert(2, ".").insert(1, ".").toString()
            }
            properties["Vending.versionString"] = vendingVersionString
        }
        if (properties.containsKey("Build.FINGERPRINT") && (!properties.containsKey("Build.ID") || !properties.containsKey("Build.VERSION.RELEASE"))) {
            val fingerprint = properties.getProperty("Build.FINGERPRINT").split("/".toRegex()).toTypedArray()
            var buildId: String? = ""
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