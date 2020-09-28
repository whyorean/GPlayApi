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

object Constants {
    const val AVAILABILITY_NOT_RESTRICTED = 1
    const val AVAILABILITY_RESTRICTED_GEO = 2
    const val AVAILABILITY_REMOVED = 7
    const val AVAILABILITY_INCOMPATIBLE_DEVICE_APP = 9
    const val IMAGE_TYPE_APP_SCREENSHOT = 1
    const val IMAGE_TYPE_PLAY_STORE_PAGE_BACKGROUND = 2
    const val IMAGE_TYPE_YOUTUBE_VIDEO_LINK = 3
    const val IMAGE_TYPE_APP_ICON = 4
    const val IMAGE_TYPE_CATEGORY_ICON = 5
    const val IMAGE_TYPE_GOOGLE_PLUS_BACKGROUND = 15

    enum class ABUSE(var value: Int) {
        SEXUAL_CONTENT(1), GRAPHIC_VIOLENCE(3), HATEFUL_OR_ABUSIVE_CONTENT(4), IMPROPER_CONTENT_RATING(5), HARMFUL_TO_DEVICE_OR_DATA(7), OTHER(8), ILLEGAL_PRESCRIPTION(11), IMPERSONATION(12);
    }

    enum class PATCH_FORMAT(var value: Int) {
        GDIFF(1), GZIPPED_GDIFF(2), GZIPPED_BSDIFF(3), UNKNOWN_4(4), UNKNOWN_5(5);
    }

    enum class REVIEW_SORT(var value: Int) {
        NEWEST(0), HIGH_RATING(1), HELPFUL(4);
    }

    enum class RECOMMENDATION_TYPE(var value: Int) {
        ALSO_VIEWED(1), ALSO_INSTALLED(2);
    }

    enum class SEARCH_SUGGESTION_TYPE(var value: Int) {
        SEARCH_STRING(2), APP(3);
    }

    enum class SUBCATEGORY(var value: String) {
        TOP_FREE("apps_topselling_free"), TOP_GROSSING("apps_topgrossing"), MOVERS_SHAKERS("apps_movers_shakers");
    }

    enum class LIBRARY_ID(var value: String) {
        WISH_LIST("u-wl");
    }

    enum class APP_STREAM_TAB {
        INSTALLED, UPDATES, LIBRARY
    }

    enum class Restriction(val restriction: Int) {
        GENERIC(-1), NOT_RESTRICTED(AVAILABILITY_NOT_RESTRICTED), RESTRICTED_GEO(AVAILABILITY_RESTRICTED_GEO), INCOMPATIBLE_DEVICE(AVAILABILITY_INCOMPATIBLE_DEVICE_APP);

        companion object {
            fun forInt(restriction: Int): Restriction {
                return when (restriction) {
                    AVAILABILITY_NOT_RESTRICTED -> NOT_RESTRICTED
                    AVAILABILITY_RESTRICTED_GEO -> RESTRICTED_GEO
                    AVAILABILITY_INCOMPATIBLE_DEVICE_APP -> INCOMPATIBLE_DEVICE
                    else -> GENERIC
                }
            }
        }
    }
}