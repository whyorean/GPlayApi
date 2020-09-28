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

package com.aurora.gplayapi.data.builders

import com.aurora.gplayapi.Constants
import com.aurora.gplayapi.data.models.Review

object ReviewBuilder {
    @JvmStatic
    fun build(reviewProto: com.aurora.gplayapi.Review): Review {
        val review = Review()
        review.comment = reviewProto.comment
        review.title = reviewProto.title
        review.rating = reviewProto.starRating
        review.userName = reviewProto.userProfile.name
        review.timeStamp = reviewProto.timestamp
        review.appVersion = reviewProto.version

        for (image in reviewProto.userProfile.imageList) {
            if (image.imageType == Constants.IMAGE_TYPE_APP_ICON) {
                review.userPhotoUrl = image.imageUrl
            }
        }
        return review
    }
}