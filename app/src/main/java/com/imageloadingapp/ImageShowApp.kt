package com.imageloadingapp

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class ImageShowApp : Application() {


    companion object {
        lateinit var instance: Application
        lateinit var appContext: Context

    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        appContext = applicationContext


    }
    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }
}

