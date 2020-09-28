package com.aurora.gplayapi.data.models

data class Rating(
        var average: Float = 0f,
        var oneStar: Long = 0,
        var twoStar: Long = 0,
        var threeStar: Long = 0,
        var fourStar: Long = 0,
        var fiveStar: Long = 0,
        var thumbsUp: Long = 0,
        var thumbsDown: Long = 0,
        var label: String = String(),
        var abbreviatedLabel: String = String()
)
