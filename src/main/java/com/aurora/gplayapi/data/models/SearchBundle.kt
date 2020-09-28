package com.aurora.gplayapi.data.models

import java.util.*

class SearchBundle {
    var query: String = String()
    var suggestionTerms: MutableSet<String> = HashSet()
    var subBundles: MutableSet<SubBundle> = HashSet()
    var appList: MutableList<App> = ArrayList()

    enum class Type {
        GENERIC, SIMILAR, RELATED_SEARCHES, RELATED_TO_YOUR_SEARCH, YOU_MIGHT_ALSO_LIKE, BOGUS
    }

    class SubBundle(var nextPageUrl: String, var type: Type)
}