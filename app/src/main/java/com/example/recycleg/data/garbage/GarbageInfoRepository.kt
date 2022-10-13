package com.example.recycleg.data.garbage

import com.example.recycleg.data.Result
import com.example.recycleg.model.GarbageInfo
import com.example.recycleg.model.GarbageType

interface GarbageInfoRepository {
    suspend fun getGarbageInfo(garbageType: GarbageType): Result<GarbageInfo>
}