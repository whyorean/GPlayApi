package com.aurora.gplayapi.data.models

class File(
        var name: String = String(),
        var url: String = String(),
        var size: Long = 0L,
        var type: FileType = FileType.BASE) {
    enum class FileType {
        BASE, OBB, PATCH, SPLIT
    }
}