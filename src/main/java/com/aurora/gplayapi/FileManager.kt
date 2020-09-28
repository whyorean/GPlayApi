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

import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

object FileManager {
    fun loadPackages(fileName: String?): List<String>? {
        return try {
            val stringList: MutableList<String> = ArrayList()
            val inputStream = javaClass.classLoader.getResourceAsStream(fileName)
            val br = BufferedReader(InputStreamReader(inputStream))
            var strLine: String
            while (br.readLine().also { strLine = it } != null) {
                stringList.add(strLine)
            }
            stringList
        } catch (e: Exception) {
            null
        }
    }
}