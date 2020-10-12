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
import com.aurora.gplayapi.data.builders.AppBuilder.build
import com.aurora.gplayapi.data.models.*
import com.aurora.gplayapi.data.models.editor.EditorChoiceBundle
import com.aurora.gplayapi.data.models.editor.EditorChoiceCluster
import com.aurora.gplayapi.data.models.subcategory.SubCategoryBundle
import com.aurora.gplayapi.data.models.subcategory.SubCategoryCluster
import com.aurora.gplayapi.data.providers.HeaderProvider.getDefaultHeaders
import com.aurora.gplayapi.network.HttpClient
import java.io.IOException
import java.util.*

open class BaseHelper(protected var authData: AuthData) {
    @Throws(IOException::class)
    fun getResponse(url: String, params: Map<String, String>, headers: Map<String, String>): PlayResponse {
        return HttpClient.get(url, headers, params)
    }

    /*-------------------------------------------- COMMONS -------------------------------------------------*/
    private fun getNextPageUrl(item: Item): String {
        return if (item.hasContainerMetadata() && item.containerMetadata.hasNextPageUrl()) item.containerMetadata.nextPageUrl else String()
    }

    private fun getBrowseUrl(item: Item): String {
        return if (item.hasContainerMetadata() && item.containerMetadata.hasBrowseUrl()) item.containerMetadata.browseUrl else String()
    }

    @Throws(Exception::class)
    fun getPayLoadFromBytes(bytes: ByteArray?): Payload {
        val responseWrapper = ResponseWrapper.parseFrom(bytes)
        return responseWrapper!!.payload
    }

    @Throws(Exception::class)
    fun getDetailsResponseFromBytes(bytes: ByteArray?): DetailsResponse {
        val payload = getPayLoadFromBytes(bytes)
        return payload.detailsResponse
    }

    @Throws(Exception::class)
    fun getListResponseFromBytes(bytes: ByteArray?): ListResponse? {
        val payload = getPayLoadFromBytes(bytes)
        return payload.listResponse
    }

    @Throws(Exception::class)
    fun getPrefetchPayLoad(bytes: ByteArray?): Payload {
        val responseWrapper = ResponseWrapper.parseFrom(bytes)
        val payload = responseWrapper.payload
        return if (responseWrapper.preFetchCount > 0 && ((payload.hasSearchResponse()
                        && payload.searchResponse.itemCount == 0)
                        || payload.hasListResponse() && payload.listResponse.itemCount == 0
                        || payload.hasBrowseResponse())) {
            responseWrapper.getPreFetch(0).response.payload
        } else payload
    }

    fun getAppsFromItem(item: Item): List<App> {
        val appList: MutableList<App> = ArrayList()
        if (item.subItemCount > 0) {
            for (subItem in item.subItemList) {
                if (subItem.type == 1) {
                    val app = build(subItem)
                    appList.add(app)
                    //System.out.printf("%s -> %s\n", app.getDisplayName(), app.getPackageName());
                }
            }
        }
        return appList
    }

    fun getBulkDetailsBytes(packageList: List<String?>?): ByteArray {
        val bulkDetailsRequestBuilder = BulkDetailsRequest.newBuilder()
        bulkDetailsRequestBuilder.addAllDocId(packageList)
        return bulkDetailsRequestBuilder.build().toByteArray()
    }

    /*-------------------------------------- APP SEARCH & SUGGESTIONS ---------------------------------------*/
    @Throws(Exception::class)
    fun getSearchResponseFromBytes(bytes: ByteArray?): SearchResponse? {
        val payload = getPayLoadFromBytes(bytes)
        return if (payload.hasSearchResponse()) {
            payload.searchResponse
        } else null
    }

    @Throws(Exception::class)
    fun getSearchSuggestResponseFromBytes(bytes: ByteArray?): SearchSuggestResponse? {
        val payload = getPayLoadFromBytes(bytes)
        return if (payload.hasSearchSuggestResponse()) {
            payload.searchSuggestResponse
        } else null
    }

    /*--------------------------------------- GENERIC APP STREAMS --------------------------------------------*/
    @Throws(Exception::class)
    fun getNextStreamResponse(nextPageUrl: String): ListResponse? {
        val headers: Map<String, String> = getDefaultHeaders(authData)
        val playResponse = HttpClient.get(GooglePlayApi.URL_FDFE + "/" + nextPageUrl, headers)
        return getListResponseFromBytes(playResponse.responseBytes)
    }

    @Throws(Exception::class)
    fun getNextStreamCluster(nextPageUrl: String): StreamCluster? {
        val listResponse = getNextStreamResponse(nextPageUrl)
        return getStreamCluster(listResponse)
    }

    fun getStreamCluster(item: Item): StreamCluster {
        val title = if (item.hasTitle()) item.title else String()
        val subtitle = if (item.hasSubtitle()) item.subtitle else String()
        val streamCluster = StreamCluster()
        streamCluster.title = title
        streamCluster.subtitle = subtitle
        streamCluster.browseUrl = getBrowseUrl(item)
        streamCluster.nextPageUrl = getNextPageUrl(item)
        streamCluster.appList = getAppsFromItem(item)
        return streamCluster
    }

    fun getStreamCluster(payload: Payload): StreamCluster? {
        return if (payload.hasListResponse()) getStreamCluster(payload.listResponse) else null
    }

    fun getStreamCluster(listResponse: ListResponse?): StreamCluster? {
        if (listResponse!!.itemCount > 0) {
            val item = listResponse.getItem(0)
            if (item != null && item.subItemCount > 0) {
                val subItem = item.getSubItem(0)
                return getStreamCluster(subItem)
            }
        }
        return null
    }

    fun getStreamClusters(listResponse: ListResponse): List<StreamCluster> {
        val streamClusters: MutableList<StreamCluster> = ArrayList()
        if (listResponse.itemCount > 0) {
            val item = listResponse.getItem(0)
            if (item != null && item.subItemCount > 0) {
                for (subItem in item.subItemList) {
                    streamClusters.add(getStreamCluster(subItem))
                }
            }
        }
        return streamClusters
    }

    fun getStreamBundle(listResponse: ListResponse): StreamBundle {
        var nextPageUrl = String()
        val streamClusters: MutableList<StreamCluster> = ArrayList()
        if (listResponse.itemCount > 0) {
            val item = listResponse.getItem(0)
            if (item != null && item.subItemCount > 0) {
                for (subItem in item.subItemList) {
                    streamClusters.add(getStreamCluster(subItem))
                }
                nextPageUrl = getNextPageUrl(item)
            }
        }
        return StreamBundle("", nextPageUrl, streamClusters)
    }

    /*------------------------------------- SUBCATEGORY STREAMS & BUNDLES ------------------------------------*/
    fun getSubCategoryCluster(item: Item): SubCategoryCluster {
        val title = if (item.hasTitle()) item.title else String()
        val subCategoryCluster = SubCategoryCluster()
        subCategoryCluster.title = title
        subCategoryCluster.browseUrl = getBrowseUrl(item)
        subCategoryCluster.nextPageUrl = getNextPageUrl(item)
        subCategoryCluster.appList = getAppsFromItem(item)
        return subCategoryCluster
    }

    fun getSubCategoryBundle(payload: Payload): SubCategoryBundle {
        var nextPageUrl = String()
        val subCategoryClusters: MutableList<SubCategoryCluster> = ArrayList()
        if (payload.hasListResponse() && payload.listResponse.itemCount > 0) {
            val item = payload.listResponse.getItem(0)
            if (item != null) {
                if (item.subItemCount > 0) {
                    for (subItem in item.subItemList) {
                        subCategoryClusters.add(getSubCategoryCluster(subItem))
                    }
                }
                nextPageUrl = getNextPageUrl(item)
            }
        }
        val subCategoryBundle = SubCategoryBundle()
        subCategoryBundle.subCategoryClusters = subCategoryClusters
        subCategoryBundle.nextPageUrl = nextPageUrl
        return subCategoryBundle
    }

    @Throws(Exception::class)
    fun getSubCategoryCluster(bytes: ByteArray?): SubCategoryBundle? {
        val responseWrapper = ResponseWrapper.parseFrom(bytes)
        if (responseWrapper.preFetchCount > 0) {
            val preFetch = responseWrapper.getPreFetch(0)
            if (preFetch.hasResponse() && preFetch.response.hasPayload()) {
                val payload = preFetch.response.payload
                return getSubCategoryBundle(payload)
            }
        } else if (responseWrapper.hasPayload()) {
            val payload = responseWrapper.payload
            return getSubCategoryBundle(payload)
        }
        return null
    }

    /*------------------------------------- EDITOR'S CHOICE CLUSTER & BUNDLES ------------------------------------*/
    @Throws(Exception::class)
    fun getEditorChoiceBrowseResponse(nextPageUrl: String): ListResponse {
        val headers: Map<String, String> = getDefaultHeaders(authData)
        val responseBody = HttpClient.get(GooglePlayApi.URL_FDFE + "/" + nextPageUrl, headers)
        val payload = getPrefetchPayLoad(responseBody.responseBytes)
        return payload.listResponse
    }

    fun getEditorChoiceApps(listResponse: ListResponse?): List<App> {
        val appList: MutableList<App> = ArrayList()
        if (listResponse != null && listResponse.itemCount > 0) {
            val item = listResponse.getItem(0)
            if (item != null && item.subItemCount > 0) {
                for (subItem in item.subItemList) {
                    if (subItem.subItemCount > 0) {
                        appList.add(build(subItem.getSubItem(0)))
                    }
                }
            }
        }
        return appList
    }

    fun getEditorChoiceCluster(item: Item): EditorChoiceCluster {
        val title = if (item.hasTitle()) item.title else String()
        val imageList: MutableList<Image> = ArrayList()
        if (item.imageCount > 0) imageList.addAll(item.imageList)
        val editorChoiceCluster = EditorChoiceCluster()
        editorChoiceCluster.title = title
        editorChoiceCluster.browseUrl = getBrowseUrl(item)
        editorChoiceCluster.imageList = imageList
        return editorChoiceCluster
    }

    fun getEditorChoiceBundles(item: Item): EditorChoiceBundle {
        val title = if (item.hasTitle()) item.title else String()
        val choiceClusters: MutableList<EditorChoiceCluster> = ArrayList()
        for (subItem in item.subItemList) {
            choiceClusters.add(getEditorChoiceCluster(subItem))
        }
        val editorChoiceBundle = EditorChoiceBundle()
        editorChoiceBundle.title = title
        editorChoiceBundle.choiceClusters = choiceClusters
        return editorChoiceBundle
    }

    fun getEditorChoiceBundles(listResponse: ListResponse?): List<EditorChoiceBundle> {
        val editorChoiceBundles: MutableList<EditorChoiceBundle> = ArrayList()
        if (listResponse != null && listResponse.itemCount > 0) {
            val item = listResponse.getItem(0)
            if (item != null) {
                if (item.subItemCount > 0) {
                    for (subItem in item.subItemList) {
                        editorChoiceBundles.add(getEditorChoiceBundles(subItem))
                    }
                }
            }
        }
        return editorChoiceBundles
    }
}