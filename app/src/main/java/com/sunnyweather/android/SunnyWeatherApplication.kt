package com.sunnyweather.android

import android.app.Application
import android.content.Context
import com.nostra13.universalimageloader.core.ImageLoader
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager

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
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
            //使用ExoPlayer解码
            .setPlayerFactory(ExoMediaPlayerFactory.create())
            .build());
    }
}