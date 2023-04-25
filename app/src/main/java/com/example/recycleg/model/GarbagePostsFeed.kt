package com.example.recycleg.model

data class GarbagePostsFeed(
    val reducedInfo: List<GarbageInfoPost>,
    val info: List<GarbageInfoPost>
) {
    val allInfoPosts: List<GarbageInfoPost> = reducedInfo + info
}
