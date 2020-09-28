package com.aurora.gplayapi.helpers

import com.aurora.gplayapi.GooglePlayApi
import com.aurora.gplayapi.Item
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.Category
import com.aurora.gplayapi.data.models.subcategory.SubCategoryBundle
import com.aurora.gplayapi.data.providers.HeaderProvider
import com.aurora.gplayapi.network.HttpClient
import java.util.*

class CategoryHelper(authData: AuthData) : BaseHelper(authData) {

    @Throws(Exception::class)
    fun getAllCategoriesList(type: Category.Type): List<Category> {
        val categoryList: MutableList<Category> = ArrayList()
        val headers = HeaderProvider.getDefaultHeaders(authData)
        val params: MutableMap<String, String> = HashMap()
        params["c"] = "3"
        params["cat"] = type.value

        val responseBody = HttpClient[GooglePlayApi.CATEGORIES_URL, headers, params]
        val listResponse = getListResponseFromBytes(responseBody?.bytes())

        if (listResponse!!.itemCount > 0) {
            val item = listResponse.getItem(0)
            if (item.subItemCount > 0) {
                val subItem = item.getSubItem(0)
                if (subItem.subItemCount > 0) {
                    for (subSubItem in subItem.subItemList) {
                        categoryList.add(getCategoryFromItem(type, subSubItem))
                    }
                }
            }
        }
        return categoryList
    }

    @Throws(Exception::class)
    fun getSubCategoryCluster(homeUrl: String): SubCategoryBundle? {
        val headers = HeaderProvider.getDefaultHeaders(authData)
        val responseBody = HttpClient[GooglePlayApi.URL_FDFE + "/" + homeUrl, headers]
        if (responseBody != null) {
            return getSubCategoryCluster(responseBody.bytes())
        }
        return null
    }

    private fun getCategoryFromItem(type: Category.Type, subItem: Item): Category {
        val category = Category()
        category.title = subItem.title
        category.imageUrl = subItem.getImage(0).imageUrl
        category.color = subItem.getImage(0).color
        category.homeUrl = subItem.relatedLinks.unknown1.unknown2.homeUrl
        category.type = type
        return category
    }
}