package com.aurora.gplayapi.data.providers

import com.aurora.gplayapi.data.models.AuthData
import java.util.*

object HeaderProvider {

    fun getAuthHeaders(builder: AuthData): MutableMap<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["app"] = "com.google.android.gms"
        headers["User-Agent"] = builder.deviceInfoProvider!!.authUserAgentString
        if (builder.gsfId.isNotBlank())
            headers["device"] = builder.gsfId
        return headers
    }

    fun getDefaultHeaders(authData: AuthData): MutableMap<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["Authorization"] = "Bearer " + authData.authToken
        headers["User-Agent"] = authData.deviceInfoProvider!!.userAgentString
        headers["X-DFE-Device-Id"] = authData.gsfId
        headers["Accept-Language"] = authData.locale.toString().replace("_", "-")
        headers["X-DFE-Encoded-Targets"] = "CAESN/qigQYC2AMBFfUbyA7SM5Ij/CvfBoIDgxHqGP8R3xzIBvoQtBKFDZ4HAY4FrwSVMasHBO0O2Q8akgYRAQECAQO7AQEpKZ0CnwECAwRrAQYBr9PPAoK7sQMBAQMCBAkIDAgBAwEDBAICBAUZEgMEBAMLAQEBBQEBAcYBARYED+cBfS8CHQEKkAEMMxcBIQoUDwYHIjd3DQ4MFk0JWGYZEREYAQOLAYEBFDMIEYMBAgICAgICOxkCD18LGQKEAcgDBIQBAgGLARkYCy8oBTJlBCUocxQn0QUBDkkGxgNZQq0BZSbeAmIDgAEBOgGtAaMCDAOQAZ4BBIEBKUtQUYYBQscDDxPSARA1oAEHAWmnAsMB2wFyywGLAxol+wImlwOOA80CtwN26A0WjwJVbQEJPAH+BRDeAfkHK/ABASEBCSAaHQemAzkaRiu2Ad8BdXeiAwEBGBUBBN4LEIABK4gB2AFLfwECAdoENq0CkQGMBsIBiQEtiwGgA1zyAUQ4uwS8AwhsvgPyAcEDF27vApsBHaICGhl3GSKxAR8MC6cBAgItmQYG9QIeywLvAeYBDArLAh8HASI4ELICDVmVBgsY/gHWARtcAsMBpALiAdsBA7QBpAJmIArpByn0AyAKBwHTARIHAX8D+AMBcRIBBbEDmwUBMacCHAciNp0BAQF0OgQLJDuSAh54kwFSP0eeAQQ4M5EBQgMEmwFXywFo0gFyWwMcapQBBugBPUW2AVgBKmy3AR6PAbMBGQxrUJECvQR+8gFoWDsYgQNwRSczBRXQAgtRswEW0ALMAREYAUEBIG6yATYCRE8OxgER8gMBvQEDRkwLc8MBTwHZAUOnAXiiBakDIbYBNNcCIUmuArIBSakBrgFHKs0EgwV/G3AD0wE6LgECtQJ4xQFwFbUCjQPkBS6vAQqEAUZF3QIM9wEhCoYCQhXsBCyZArQDugIziALWAdIBlQHwBdUErQE6qQaSA4EEIvYBHir9AQVLmgMCApsCKAwHuwgrENsBAjNYswEVmgIt7QJnN4wDEnta+wGfAcUBxgEtEFXQAQWdAUAeBcwBAQM7rAEJATJ0LENrdh73A6UBhAE+qwEeASxLZUMhDREuH0CGARbd7K0GlQo"
        headers["X-DFE-Phenotype"] = "H4sIAAAAAAAAAB3OO3KjMAAA0KRNuWXukBkBQkAJ2MhgAZb5u2GCwQZbCH_EJ77QHmgvtDtbv-Z9_H63zXXU0NVPB1odlyGy7751Q3CitlPDvFd8lxhz3tpNmz7P92CFw73zdHU2Ie0Ad2kmR8lxhiErTFLt3RPGfJQHSDy7Clw10bg8kqf2owLokN4SecJTLoSwBnzQSd652_MOf2d1vKBNVedzg4ciPoLz2mQ8efGAgYeLou-l-PXn_7Sna1MfhHuySxt-4esulEDp8Sbq54CPPKjpANW-lkU2IZ0F92LBI-ukCKSptqeq1eXU96LD9nZfhKHdtjSWwJqUm_2r6pMHOxk01saVanmNopjX3YxQafC4iC6T55aRbC8nTI98AF_kItIQAJb5EQxnKTO7TZDWnr01HVPxelb9A2OWX6poidMWl16K54kcu_jhXw-JSBQkVcD_fPsLSZu6joIBAAA"
        headers["X-DFE-Client-Id"] = "am-android-google"
        headers["X-DFE-Network-Type"] = "4"
        headers["X-DFE-Content-Filters"] = ""
        headers["X-Limit-Ad-Tracking-Enabled"] = "false"
        headers["X-Ad-Id"] = "LawadaMera"
        headers["X-DFE-UserLanguages"] = authData.locale.toString()
        headers["X-DFE-Request-Params"] = "timeoutMs=4000"

        if (authData.deviceCheckInConsistencyToken.isNotBlank()) {
            headers["X-DFE-Device-Checkin-Consistency-Token"] = authData.deviceCheckInConsistencyToken
        }

        if (authData.deviceConfigToken.isNotBlank()) {
            headers["X-DFE-Device-Config-Token"] = authData.deviceConfigToken
        }

        if (authData.dfeCookie.isNotBlank()) {
            headers["X-DFE-Cookie"] = authData.dfeCookie
        }

        val mccMnc = authData.deviceInfoProvider!!.mccMnc
        if (mccMnc.isNotBlank()) {
            headers["X-DFE-MCCMNC"] = mccMnc
        }
        return headers
    }

    fun getAASTokenHeaders(email: String, oauthToken: String): MutableMap<String, String> {
        val headers: MutableMap<String, String> = HashMap()
        headers["service"] = "ac2dm"
        headers["add_account"] = "1"
        headers["get_accountid"] = "1"
        headers["ACCESS_TOKEN"] = "1"
        headers["callerPkg"] = "com.google.android.gms"
        headers["Token"] = oauthToken
        return headers
    }
}