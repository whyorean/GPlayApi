package com.aurora.gplayapi.data.models

class StreamBundle (
    val title: String = String(),
    val nextPageUrl: String = String(),
    val streamClusters: List<StreamCluster> = ArrayList()
)