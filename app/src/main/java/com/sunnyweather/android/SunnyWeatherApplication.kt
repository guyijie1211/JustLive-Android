package com.sunnyweather.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.drake.brv.utils.BRV
import com.sunnyweather.android.logic.model.UserInfo
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory

class SunnyWeatherApplication : Application() {
    companion object {
        lateinit var context: Context
        var areaName = MutableLiveData<String>()
        var areaType = MutableLiveData<String>()
        var userInfo: UserInfo? = null
        var isLogin = MutableLiveData(false)

        fun isNightMode(context: Context): Boolean {
            val uiMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return uiMode == Configuration.UI_MODE_NIGHT_YES
        }

        fun clearLoginInfo(activity: Activity) {
            if (!isLogin.value!!) {
                Toast.makeText(context, "未登录", Toast.LENGTH_SHORT).show()
                return
            }
            var sharedPref = activity.getSharedPreferences("JustLive", Context.MODE_PRIVATE)
            sharedPref.edit().remove("username").remove("password").commit()
            userInfo = null
            isLogin.value = false
            MobclickAgent.onProfileSignOff()//友盟账号退出
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
        /**
         * 获取应用程序版本名称信息
         * @param context
         * @return 当前应用的版本名称
         */
        @Synchronized
        fun getVersionCode(context: Context): Int {
            try {
                val packageManager = context.packageManager
                val packageInfo = packageManager.getPackageInfo(
                    context.packageName, 0
                )
                return packageInfo.versionCode
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 0
        }
    }

    override fun onCreate() {
        super.onCreate()
        UMConfigure.preInit(this, "6159ddaf14e22b6a4f146772", "QQ群")
        UMConfigure.init(this,"6159ddaf14e22b6a4f146772","QQ群",UMConfigure.DEVICE_TYPE_PHONE, "")
        context = applicationContext
        BRV.modelId = BR.areaFollow
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
            //使用ExoPlayer解码
            .setPlayerFactory(ExoMediaPlayerFactory.create())
            .build())
    }
}