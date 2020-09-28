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