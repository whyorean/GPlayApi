package com.aurora.gplayapi.data.models

class Category {
    var title: String = String()
    var imageUrl: String = String()
    var homeUrl: String = String()
    var color: String = String()
    var type: Type = Type.APPLICATION

    enum class Type(val value:String) {
        GAME("GAME"), APPLICATION("APPLICATION");
    }
}