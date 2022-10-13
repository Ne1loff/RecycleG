package com.example.recycleg.data.garbage.impl

import com.example.recycleg.data.Result
import com.example.recycleg.data.garbage.GarbageInfoRepository
import com.example.recycleg.model.GarbageInfo
import com.example.recycleg.model.GarbageType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FakeGarbageInfoRepository: GarbageInfoRepository {
    override suspend fun getGarbageInfo(garbageType: GarbageType): Result<GarbageInfo> {
        return withContext(Dispatchers.IO) {
            val garbage = garbageFeed.allInfo.find { it.type == garbageType }
            if (garbage == null) {
                Result.Error(IllegalArgumentException("Post not found"))
            } else {
                Result.Success(garbage)
            }
        }
    }
}