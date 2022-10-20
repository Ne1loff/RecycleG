package com.example.recycleg.data.garbage

import com.example.recycleg.data.Result
import com.example.recycleg.model.GarbageInfoPost
import com.example.recycleg.model.GarbagePostsFeed
import com.example.recycleg.model.GarbageType

interface GarbageInfoPostsRepository {
    suspend fun getGarbageInfoPost(garbageType: GarbageType): Result<GarbageInfoPost>
    suspend fun getGarbagePostsFeed(): Result<GarbagePostsFeed>
}