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

    const val IMAGE_TYPE_APP_SCREENSHOT = 1
    const val IMAGE_TYPE_PLAY_STORE_PAGE_BACKGROUND = 2
    const val IMAGE_TYPE_YOUTUBE_VIDEO_LINK = 3
    const val IMAGE_TYPE_APP_ICON = 4
    const val IMAGE_TYPE_CATEGORY_ICON = 5
    const val IMAGE_TYPE_GOOGLE_PLUS_BACKGROUND = 15

    enum class ABUSE(var value: Int) {
        SEXUAL_CONTENT(1),
        GRAPHIC_VIOLENCE(3),
        HATEFUL_OR_ABUSIVE_CONTENT(4),
        IMPROPER_CONTENT_RATING(5),
        HARMFUL_TO_DEVICE_OR_DATA(7),
        OTHER(8), ILLEGAL_PRESCRIPTION(11),
        IMPERSONATION(12);
    }

    enum class PATCH_FORMAT(var value: Int) {
        GDIFF(1),
        GZIPPED_GDIFF(2),
        GZIPPED_BSDIFF(3),
        UNKNOWN_4(4),
        UNKNOWN_5(5);
    }

    enum class Restriction(val restriction: Int) {
        GENERIC(-1),
        NOT_RESTRICTED(1),
        GEO_RESTRICTED(2),
        DEVICE_RESTRICTED(7),
        UNKNOWN(9);

        companion object {
            fun forInt(restriction: Int): Restriction {
                return when (restriction) {
                    1 -> NOT_RESTRICTED
                    2 -> GEO_RESTRICTED
                    7 -> DEVICE_RESTRICTED
                    9 -> UNKNOWN
                    else -> GENERIC
                }
            }
        }
    }
}