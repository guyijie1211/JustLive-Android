package com.sunnyweather.android

import android.app.Application
import android.content.Context
import com.nostra13.universalimageloader.core.ImageLoader

class SunnyWeatherApplication : Application() {
    companion object {
        const val TOKEN = "4JHO0bP5s6SeRJ1D"
        const val LANGUAGE = "zh_CN"
        lateinit var context: Context
        val imageLoader: ImageLoader? = ImageLoader.getInstance() // Get singleton instance
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}