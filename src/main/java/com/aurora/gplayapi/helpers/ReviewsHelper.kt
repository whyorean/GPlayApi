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
import com.aurora.gplayapi.ReviewResponse
import com.aurora.gplayapi.UserReviewsResponse
import com.aurora.gplayapi.data.builders.ReviewBuilder.build
import com.aurora.gplayapi.data.models.AuthData
import com.aurora.gplayapi.data.models.Review
import com.aurora.gplayapi.data.providers.HeaderProvider.getDefaultHeaders
import com.aurora.gplayapi.network.HttpClient
import java.util.*

class ReviewsHelper(authData: AuthData?) : BaseHelper(authData!!) {
    private var offset = 1

    @Throws(Exception::class)
    private fun getReviewResponse(url: String, params: Map<String, String>, headers: Map<String, String>): ReviewResponse? {
        val responseBody = getResponse(url, headers, params)
        val payload = getPayLoadFromBytes(responseBody!!.bytes())
        return if (payload != null && payload.hasReviewResponse()) payload.reviewResponse else null
    }

    @Throws(Exception::class)
    private fun postReviewResponse(params: Map<String, String>, headers: Map<String, String>): ReviewResponse? {
        val responseBody = HttpClient.post(GooglePlayApi.URL_REVIEW_ADD_EDIT, headers, params)!!
        val payload = getPayLoadFromBytes(responseBody.bytes())
        return if (payload != null && payload.hasReviewResponse()) payload.reviewResponse else null
    }

    private fun getUserReviewsResponse(reviewResponse: ReviewResponse?): UserReviewsResponse? {
        return reviewResponse?.userReviewsResponse
    }

    @Throws(Exception::class)
    fun getReviews(packageName: String, filter: Review.Filter, offset: Int, resultNum: Int): List<Review> {
        val params: MutableMap<String, String> = HashMap()
        params["doc"] = packageName
        params["o"] = offset.toString()
        params["n"] = resultNum.toString()
        when (filter) {
            Review.Filter.ALL -> params["sfilter"] = filter.value
            Review.Filter.POSITIVE, Review.Filter.CRITICAL -> params["sent"] = filter.value
            else -> params["rating"] = filter.value
        }
        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        val reviewList: MutableList<Review> = ArrayList()
        val reviewResponse = getReviewResponse(GooglePlayApi.URL_REVIEWS, params, headers)
        val userReviewsResponse = getUserReviewsResponse(reviewResponse)
        if (userReviewsResponse != null) {
            for (reviewProto in userReviewsResponse.reviewList) {
                reviewList.add(build(reviewProto!!))
            }
        }
        return reviewList
    }

    @Throws(Exception::class)
    fun getUserReview(packageName: String, testing: Boolean): Review? {
        val params: MutableMap<String, String> = HashMap()
        params["doc"] = packageName
        params["itpr"] = if (testing) "true" else "false"
        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        val reviewResponse = getReviewResponse(GooglePlayApi.URL_REVIEWS, params, headers)
        val userReviewsResponse = getUserReviewsResponse(reviewResponse)
        if (userReviewsResponse != null) {
            if (userReviewsResponse.reviewCount > 0) {
                val review = userReviewsResponse.getReview(0)
                return build(review)
            }
        }
        return null
    }

    @Throws(Exception::class)
    fun addOrEditReview(packageName: String, title: String, content: String, rating: Int, isBeta: Boolean): Review? {
        val params: MutableMap<String, String> = HashMap()
        params["doc"] = packageName
        params["title"] = title
        params["content"] = content
        params["rating"] = rating.toString()
        params["rst"] = "3"
        params["itpr"] = if (isBeta) "true" else "false"

        val headers: MutableMap<String, String> = getDefaultHeaders(authData)
        val reviewResponse = postReviewResponse(params, headers)
        if (reviewResponse != null) {
            val reviewProto = reviewResponse.userReview
            if (reviewProto != null) {
                return build(reviewProto)
            }
        }
        return null
    }

    @Throws(Exception::class)
    fun next(packageName: String, filter: Review.Filter): List<Review> {
        return getReviews(packageName, filter, ++offset * DEFAULT_SIZE, DEFAULT_SIZE)
    }

    companion object {
        const val DEFAULT_SIZE = 20
    }
}