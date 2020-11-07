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

import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.SingletonHolder
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.providers.HeaderProvider
import com.aurora.gplayapi.network.HttpClient

class AuthValidator private constructor(authData: AuthData) : BaseHelper(authData) {

    companion object : SingletonHolder<AuthValidator, AuthData>(::AuthValidator)

    fun isValid(): Boolean {
        val headers = HeaderProvider.getDefaultHeaders(authData)
        val playResponse = HttpClient.get(GooglePlayApi.URL_SELF_UPDATE, headers) // OR use contentSync
        return playResponse.isSuccessful
    }
}