package com.sunnyweather.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProvider
import com.nostra13.universalimageloader.core.ImageLoader
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.UserInfo
import com.sunnyweather.android.ui.login.LoginViewModel
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager
import java.lang.Exception
import java.lang.reflect.Method
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
        var isLogin = MutableLiveData(false)

        fun clearLoginInfo(activity: Activity) {
            if (!isLogin.value!!) {
                Toast.makeText(context, "未登录", Toast.LENGTH_SHORT).show()
            }
            var sharedPref = activity.getSharedPreferences("JustLive", Context.MODE_PRIVATE)
            sharedPref.edit().remove("username").remove("password").commit()
            userInfo = null
            isLogin.value = false
            Toast.makeText(context, "已退出", Toast.LENGTH_SHORT).show()
        }

        fun saveLoginInfo(activity: Activity, username: String, password: String) {
            var sharedPref = activity.getSharedPreferences("JustLive", Context.MODE_PRIVATE)
            sharedPref.edit().putString("username", username).putString("password", password).commit()
        }

        fun platformName(platform: String?): String{
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

        fun MIUISetStatusBarLightMode(activity: Activity, dark: Boolean): Boolean {
            var result = false
            val window = activity.window
            if (window != null) {
                val clazz: Class<*> = window::class.java
                try {
                    var darkModeFlag = 0
                    val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                    val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                    darkModeFlag = field.getInt(layoutParams)
                    val extraFlagField: Method = clazz.getMethod(
                        "setExtraFlags",
                        Int::class.javaPrimitiveType,
                        Int::class.javaPrimitiveType
                    )
                    if (dark) {
                        extraFlagField.invoke(window, darkModeFlag, darkModeFlag) //状态栏透明且黑色字体
                    } else {
                        extraFlagField.invoke(window, 0, darkModeFlag) //清除黑色字体
                    }
                    result = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                        //开发版 7.7.13 及以后版本采用了系统API，旧方法无效但不会报错，所以两个方式都要加上
                        if (dark) {
                            activity.window.decorView.systemUiVisibility =
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        } else {
                            activity.window.decorView.systemUiVisibility =
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        }
                    }
                } catch (e: Exception) {
                }
            }
            return result
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
    }
}