package com.example.recycleg.model

import androidx.annotation.DrawableRes

data class GarbageInfoPost(
    val type: GarbageType,
    val title: String,
    val subtitle: String? = null,
    val description: String? = null,
    @DrawableRes val imageId: Int
)

enum class GarbageType {
    Paper,
    Glass,
    Metal,
    Organic,
    Plastic
}