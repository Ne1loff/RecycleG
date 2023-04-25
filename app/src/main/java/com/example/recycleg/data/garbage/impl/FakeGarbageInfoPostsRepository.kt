package com.example.recycleg.data.garbage.impl

import com.example.recycleg.data.Result
import com.example.recycleg.data.garbage.GarbageInfoPostsRepository
import com.example.recycleg.model.GarbageInfoPost
import com.example.recycleg.model.GarbagePostsFeed
import com.example.recycleg.model.GarbageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FakeGarbageInfoPostsRepository : GarbageInfoPostsRepository {
    override suspend fun getGarbageInfoPost(garbageType: GarbageType): Result<GarbageInfoPost> {
        return withContext(Dispatchers.IO) {
            val garbage = garbagePostsFeed.allInfoPosts.find { it.type == garbageType }
            if (garbage == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(garbage)
            }
        }
    }

    override suspend fun getGarbagePostsFeed(): Result<GarbagePostsFeed> {
        return withContext(Dispatchers.IO) {
            Result.Success(garbagePostsFeed)
        }
    }
}