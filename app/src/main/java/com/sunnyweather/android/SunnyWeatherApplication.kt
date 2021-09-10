package com.sunnyweather.android

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.nostra13.universalimageloader.core.ImageLoader
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.RoomInfo
import com.sunnyweather.android.logic.model.UserInfo
import com.sunnyweather.android.logic.network.LiveNetwork
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SunnyWeatherApplication : Application() {
    companion object {
        const val TOKEN = "4JHO0bP5s6SeRJ1D"
        const val LANGUAGE = "zh_CN"
        lateinit var context: Context
        val imageLoader: ImageLoader? = ImageLoader.getInstance() // Get singleton instance
        var areaName = MutableLiveData<String>()
        var areaType = MutableLiveData<String>()
        var userInfo: UserInfo? = null
        var isLogin = false

        fun platformName(platform: String): String{
            return when(platform) {
                "douyu" -> "斗鱼"
                "huya" -> "虎牙"
                "bilibili" -> "哔哩哔哩"
                "egame" -> "企鹅电竞"
                "cc" -> "网易CC"
                else -> "未知平台"
            }
        }

        fun encodeMD5(password: String): String {
            try {
                val  instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
                val digest:ByteArray = instance.digest(password.toByteArray())//对字符串加密，返回字节数组
                var sb : StringBuffer = StringBuffer()
                for (b in digest) {
                    var i :Int = b.toInt() and 0xff//获取低八位有效值
                    var hexString = Integer.toHexString(i)//将整数转化为16进制
                    if (hexString.length < 2) {
                        hexString = "0$hexString"//如果是一位的话，补0
                    }
                    sb.append(hexString)
                }
                return sb.toString()

            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            return ""
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
            //使用ExoPlayer解码
            .setPlayerFactory(ExoMediaPlayerFactory.create())
            .build())
//        var sharedPref = this.getSharedPreferences("JustLive", Context.MODE_PRIVATE)
//        val username = sharedPref.getString("username", "").toString()
//        val password = sharedPref.getString("password", "").toString()
//        Repository.login(username, password)
    }

}