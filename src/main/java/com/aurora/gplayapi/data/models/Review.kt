package com.aurora.gplayapi.data.models

class Review {
    var title: String = String()
    var comment: String = String()
    var userName: String = String()
    var userPhotoUrl: String = String()
    var appVersion: String = String()
    var rating: Int = 0
    var timeStamp: Long = 0

    enum class Filter(val value: String) {
        ALL("ALL"), POSITIVE("1"), CRITICAL("2"), FIVE("5"), FOUR("4"), THREE("3"), TWO("2"), ONE("1");
    }
}
