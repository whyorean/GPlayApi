package com.aurora.gplayapi.network

import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.exceptions.AuthException
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.create
import java.io.IOException

object HttpClient : HttpClientImpl {

    private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(GooglePlayApi.URL_BASE)
            .build()

    private val HTTP_SERVICE: HttpService

    init {
        HTTP_SERVICE = retrofit.create()
    }

    @Throws(IOException::class)
    override fun post(url: String, headers: Map<String, String>, requestBody: RequestBody): ResponseBody? {
        val call = HTTP_SERVICE.post(url, headers, requestBody)
        val response = call.execute()
        if (response.body() == null) {
            throw AuthException(response.errorBody()?.string())
        }
        return response.body()
    }

    @Throws(IOException::class)
    override fun post(url: String, headers: Map<String, String>, params: Map<String, String>): ResponseBody? {
        val call = HTTP_SERVICE.post(url, headers, params)
        val response = call.execute()
        if (response.body() == null) {
            throw AuthException(response.errorBody()?.string())
        }
        return response.body()
    }

    @Throws(IOException::class)
    override fun get(url: String, headers: Map<String, String>): ResponseBody? {
        val call = HTTP_SERVICE[url, headers]
        val response = call.execute()
        return response.body()
    }

    @Throws(IOException::class)
    override fun get(url: String, headers: Map<String, String>, params: Map<String, String>): ResponseBody? {
        val call = HTTP_SERVICE[url, headers, params]
        val response = call.execute()
        return response.body()
    }

    @Throws(IOException::class)
    override fun getX(url: String, headers: Map<String, String>, paramString: String): ResponseBody? {
        val call = HTTP_SERVICE[url + paramString, headers]
        val response = call.execute()
        return response.body()
    }
}