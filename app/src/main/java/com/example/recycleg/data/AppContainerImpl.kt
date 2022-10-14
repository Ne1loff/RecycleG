/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.recycleg.data

import android.content.Context
import com.example.recycleg.data.garbage.GarbageInfoPostsRepository
import com.example.recycleg.data.garbage.impl.FakeGarbageInfoPostsRepository

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val garbageInfoPostsRepository: GarbageInfoPostsRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class AppContainerImpl(private val applicationContext: Context) : AppContainer {

    override val garbageInfoPostsRepository: GarbageInfoPostsRepository by lazy {
        FakeGarbageInfoPostsRepository()
    }
}
