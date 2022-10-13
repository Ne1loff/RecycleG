package com.example.recycleg.model

data class GarbageFeed(
    val reducedInfo: List<GarbageInfo>,
    val info: List<GarbageInfo>
) {
    val allInfo: List<GarbageInfo> = reducedInfo + info
}
