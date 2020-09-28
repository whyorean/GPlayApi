package com.aurora.gplayapi.data.builders

import com.aurora.gplayapi.*
import com.aurora.gplayapi.data.builders.ReviewBuilder.build
import com.aurora.gplayapi.data.models.App
import com.aurora.gplayapi.data.models.Rating
import java.util.*
import java.util.regex.Pattern

object AppBuilder {

    fun build(detailsResponse: DetailsResponse): App {
        val app = build(detailsResponse.item)
        app.footerHtml = if (detailsResponse.footerHtml.isNotBlank()) detailsResponse.footerHtml else String()
        app.features = detailsResponse.features

        if (detailsResponse.hasUserReview()) {
            app.userReview = build(detailsResponse.userReview)
        }
        return app
    }

    fun build(item: Item): App {
        val app = App()
        app.screenshotUrls = ArrayList()
        app.permissions = ArrayList()
        app.dependencies = HashSet()
        app.offerDetails = HashMap()
        app.relatedLinks = HashMap()
        app.displayName = item.title
        app.description = item.descriptionHtml
        app.shortDescription = item.descriptionShort
        app.categoryId = item.relatedLinks.categoryInfo.appCategory
        app.restriction = Constants.Restriction.forInt(item.availability.restriction)

        if (item.offerCount > 0) {
            app.offerType = item.getOffer(0).offerType
            app.isFree = item.getOffer(0).micros == 0L
            app.price = item.getOffer(0).formattedAmount
        }

        fillOfferDetails(app, item)
        fillAggregateRating(app, item.aggregateRating)
        fillRelatedLinks(app, item)

        val appDetails = item.details.appDetails
        app.fileMetadataList = appDetails.fileList
        app.packageName = appDetails.packageName
        app.versionName = appDetails.versionString
        app.versionCode = appDetails.versionCode
        app.categoryName = appDetails.categoryName
        app.size = appDetails.installationSize
        app.installs = appDetails.downloadCount
        app.downloadString = appDetails.downloadLabelAbbreviated
        app.updated = appDetails.uploadDate
        app.changes = appDetails.recentChangesHtml
        app.permissions = appDetails.permissionList
        app.containsAds = appDetails.containsAds.isNotEmpty()
        app.inPlayStore = true
        app.earlyAccess = appDetails.hasEarlyAccessInfo()
        app.testingProgramAvailable = appDetails.hasTestingProgramInfo()
        app.labeledRating = item.relatedLinks.rated.label
        app.developerName = appDetails.developerName
        app.developerEmail = appDetails.developerEmail
        app.developerAddress = appDetails.developerAddress
        app.developerWebsite = appDetails.developerWebsite

        if (app.developerName!!.isNotEmpty()) app.developerName = item.creator

        if (appDetails.hasInstantLink && appDetails.instantLink!!.isNotEmpty()) {
            app.instantAppLink = appDetails.instantLink
        }

        if (app.testingProgramAvailable) {
            app.testingProgramOptedIn = appDetails.testingProgramInfo.subscribed
            app.testingProgramEmail = appDetails.testingProgramInfo.testingProgramEmail
        }

        fillImages(app, item.imageList)
        fillDependencies(app, appDetails)
        fillOfferDetails(app, item)
        return app
    }

    private fun getInstallsNum(installsRaw: String): Long {
        val matcher = Pattern.compile("[\\d]+").matcher(installsRaw.replace("[,.\\s]+".toRegex(), ""))
        return if (matcher.find()) {
            parseSafeLong(matcher.group(0))
        } else 0
    }

    private fun fillAggregateRating(app: App, aggregateRating: AggregateRating) {
        val rating = Rating(
                aggregateRating.starRating,
                aggregateRating.oneStarRatings,
                aggregateRating.twoStarRatings,
                aggregateRating.threeStarRatings,
                aggregateRating.fourStarRatings,
                aggregateRating.fiveStarRatings,
                aggregateRating.thumbsUpCount,
                aggregateRating.thumbsUpCount,
                aggregateRating.ratingLabel,
                aggregateRating.ratingCountLabelAbbreviated
        )
        app.rating = rating
    }

    private fun fillDependencies(app: App, appDetails: AppDetails) {
        if (appDetails.dependencies.dependencyCount > 0) {
            val dependencySet: MutableSet<String> = HashSet()
            for (dependency in appDetails.dependencies.dependencyList) {
                dependencySet.add(dependency.packageName)
            }
            app.dependencies = dependencySet
        }
    }

    private fun fillOfferDetails(app: App, item: Item) {
        if (!item.hasProductDetails() || item.productDetails.sectionCount == 0) {
            return
        }
        for (productDetailsSection in item.productDetails.sectionList) {
            if (!productDetailsSection.hasContainer()) {
                continue
            }
            app.offerDetails[productDetailsSection.label] = productDetailsSection.container.description
        }
    }

    private fun fillRelatedLinks(app: App, details: Item) {
        if (!details.hasRelatedLinks()) {
            return
        }
        for (link in details.relatedLinks.relatedLinksList) {
            if (link.label.isNotEmpty() && link.url1.isNotEmpty()) {
                app.relatedLinks[link.label] = link.url1
            }
        }
    }

    private fun fillImages(app: App, images: List<Image>) {
        for (image in images) {
            when (image.imageType) {
                Constants.IMAGE_TYPE_CATEGORY_ICON -> app.categoryIconUrl = image.imageUrl
                Constants.IMAGE_TYPE_APP_ICON -> app.iconUrl = image.imageUrl
                Constants.IMAGE_TYPE_YOUTUBE_VIDEO_LINK -> app.videoUrl = image.imageUrl
                Constants.IMAGE_TYPE_PLAY_STORE_PAGE_BACKGROUND -> app.pageBackgroungUrl = image.imageUrl
                Constants.IMAGE_TYPE_APP_SCREENSHOT -> app.screenshotUrls.add(image.imageUrl)
            }
        }
    }

    private fun getLink(badge: Badge?): String? {
        return if (null != badge && badge.hasBadgeContainer1()
                && badge.badgeContainer1.hasBadgeContainer2()
                && badge.badgeContainer1.badgeContainer2.hasBadgeLinkContainer()) {
            badge.badgeContainer1.badgeContainer2.badgeLinkContainer.link
        } else null
    }

    private fun parseSafeLong(string: String): Long {
        return try {
            string.toLong()
        } catch (e: NumberFormatException) {
            0L
        }
    }
}