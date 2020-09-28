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