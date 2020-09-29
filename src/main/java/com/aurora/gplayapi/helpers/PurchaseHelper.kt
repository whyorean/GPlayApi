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

package com.aurora.gplayapi.helpers

import com.aurora.gplayapi.*
import com.aurora.gplayapi.Constants.PATCH_FORMAT
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.File
import com.aurora.gplayapi.data.providers.HeaderProvider
import com.aurora.gplayapi.network.HttpClient
import java.io.IOException
import java.util.*

class PurchaseHelper(authData: AuthData) : BaseHelper(authData) {

    companion object : SingletonHolder<PurchaseHelper, AuthData>(::PurchaseHelper)

    @Throws(Exception::class)
    fun getBuyResponse(packageName: String, versionCode: Int, offerType: Int): BuyResponse? {
        val params: MutableMap<String, String> = HashMap()
        params["ot"] = offerType.toString()
        params["doc"] = packageName
        params["vc"] = versionCode.toString()
        val responseBody = HttpClient.post(GooglePlayApi.PURCHASE_URL, HeaderProvider.getDefaultHeaders(authData), params)
        val payload = getPayLoadFromBytes(responseBody?.bytes())
        return if (payload!!.hasBuyResponse())
            payload.buyResponse
        else null
    }

    @Throws(IOException::class)
    fun getDeliveryResponse(packageName: String,
                            installedVersionCode: Int = 0,
                            updateVersionCode: Int,
                            offerType: Int,
                            patchFormats: Array<PATCH_FORMAT> = arrayOf(PATCH_FORMAT.GDIFF, PATCH_FORMAT.GZIPPED_GDIFF, PATCH_FORMAT.GZIPPED_BSDIFF),
                            downloadToken: String): DeliveryResponse? {

        val params: MutableMap<String, String> = HashMap()
        params["ot"] = offerType.toString()
        params["doc"] = packageName
        params["vc"] = updateVersionCode.toString()

        /*if (installedVersionCode > 0) {
            params["bvc"] = installedVersionCode.toString();
            params["pf"] = patchFormats[0].value.toString();
        }*/

        if (downloadToken.isNotEmpty()) {
            params["dtok"] = downloadToken
        }

        val responseBody = HttpClient[GooglePlayApi.DELIVERY_URL, HeaderProvider.getDefaultHeaders(authData), params]
        val payload = ResponseWrapper.parseFrom(responseBody?.bytes()).payload
        return if (payload != null && payload.hasDeliveryResponse()) payload.deliveryResponse else null
    }

    @Throws(Exception::class)
    fun purchase(packageName: String, versionCode: Int, offerType: Int): List<File> {
        val buyResponse = getBuyResponse(packageName, versionCode, offerType)
        val downloadToken = buyResponse!!.encodedDeliveryToken
        val deliveryResponse = getDeliveryResponse(packageName = packageName,
                updateVersionCode = versionCode,
                offerType = offerType,
                downloadToken = downloadToken)
        return getUrlsFromDeliveryResponse(packageName, deliveryResponse)
    }

    private fun getUrlsFromDeliveryResponse(packageName: String?, deliveryResponse: DeliveryResponse?): List<File> {
        val fileList: MutableList<File> = ArrayList()
        if (deliveryResponse != null) {
            //Add base apk
            val androidAppDeliveryData = deliveryResponse.appDeliveryData
            if (androidAppDeliveryData != null) {
                fileList.add(File(
                        name = String.format("%s.apk", packageName),
                        url = androidAppDeliveryData.downloadUrl,
                        size = androidAppDeliveryData.downloadSize,
                        type = File.FileType.BASE
                ))

                //Obb & patches (if any)
                val fileMetadataList = deliveryResponse.appDeliveryData.additionalFileList
                if (fileMetadataList != null) {
                    for (appFileMetadata in fileMetadataList) {
                        val isOBB = appFileMetadata.fileType == 0
                        fileList.add(File(
                                name = String.format("%s.%s.%s", if (isOBB) "main" else "patch", packageName, "obb"),
                                url = appFileMetadata.downloadUrl,
                                size = appFileMetadata.size,
                                type = if (appFileMetadata.fileType == 0) File.FileType.OBB else File.FileType.PATCH
                        ))
                    }
                }

                //Add split apks (if any)
                val splitDeliveryDataList = deliveryResponse.appDeliveryData.splitDeliveryDataList
                if (fileMetadataList != null) {
                    for (splitDeliveryData in splitDeliveryDataList) {
                        fileList.add(File(
                                name = splitDeliveryData.name,
                                url = splitDeliveryData.downloadUrl,
                                size = splitDeliveryData.downloadSize,
                                type = File.FileType.SPLIT
                        ))
                    }
                }
            }
        }
        return fileList
    }
}