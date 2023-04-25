package com.example.recycleg

import android.app.Application
import com.example.recycleg.data.AppContainer
import com.example.recycleg.data.AppContainerImpl

class RecycleGApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}