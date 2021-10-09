package com.sunnyweather.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.alibaba.fastjson.JSON
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder
import com.allenliu.versionchecklib.v2.builder.UIData
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener
import com.nostra13.universalimageloader.core.ImageLoader
import com.sunnyweather.android.logic.model.UserInfo
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory
import xyz.doikki.videoplayer.player.VideoViewConfig
import xyz.doikki.videoplayer.player.VideoViewManager
import java.lang.Exception
import java.lang.reflect.Method
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import com.sunnyweather.android.logic.model.UpdateInfo
import com.sunnyweather.android.ui.customerUIs.UpdateDialog
import java.security.AccessController.getContext
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure

class SunnyWeatherApplication : Application() {
    companion object {
        lateinit var context: Context
        var areaName = MutableLiveData<String>()
        var areaType = MutableLiveData<String>()
        var userInfo: UserInfo? = null
        var isLogin = MutableLiveData(false)
        var newestVersionNum = 0

        fun checkUpdate(ignoreVersion: Int, isCheck: Boolean) {
            AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl("https://yj1211.work:8014/api/live/versionUpdate")
                .request(object : RequestVersionListener {
                    override fun onRequestVersionSuccess(
                        downloadBuilder: DownloadBuilder?,
                        result: String?
                    ): UIData? {
                        val jsonObject = JSON.parseObject(result)
                        if (jsonObject.getInteger("code") == 200) {
                            val resultData = jsonObject.getJSONObject("data")
                            val updateInfo = JSON.toJavaObject(resultData, UpdateInfo::class.java)
                            val versionNum = getVersionCode(context)
                            newestVersionNum = updateInfo.versionNum
                            Log.i("test", newestVersionNum.toString())
                            if (versionNum == updateInfo.versionNum || ignoreVersion == updateInfo.versionNum) {
                                if (isCheck) {
                                    Toast.makeText(context, "当前已是最新版本^_^", Toast.LENGTH_SHORT).show()
                                }
                                return null
                            }
                            return UIData.create().setDownloadUrl(updateInfo.updateUrl).setContent(resultData.toJSONString())
                        }
                        return null
                    }

                    override fun onRequestVersionFailure(message: String?) {
                        Toast.makeText(context, "检查版本更新失败", Toast.LENGTH_SHORT).show()
                    }
                }).setCustomVersionDialogListener { context, versionBundle ->
                    versionBundle.content
                    val data = JSON.parseObject(versionBundle.content)
                    val updateInfo = JSON.toJavaObject(data, UpdateInfo::class.java)
                    val dialog = UpdateDialog(context, updateInfo)
                    if (isCheck) {
                        val ignoreBtn = dialog.findViewById<Button>(R.id.ignore_btn)
                        ignoreBtn.visibility = View.GONE
                        val cancelBtn = dialog.findViewById<Button>(R.id.versionchecklib_version_dialog_cancel)
                        setMargins(cancelBtn, 0, 0, 0, 40)
                    }
                    return@setCustomVersionDialogListener dialog
                }
                .setShowNotification(false)
                .setNewestVersionCode(newestVersionNum)
                .executeMission(context)
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
        fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
            if (v.layoutParams is MarginLayoutParams) {
                val p = v.layoutParams as MarginLayoutParams
                p.setMargins(l, t, r, b)
                v.requestLayout()
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
        UMConfigure.preInit(this, "******", "QQ群")
        UMConfigure.init(this,"******","QQ群",UMConfigure.DEVICE_TYPE_PHONE, "")
        context = applicationContext
        var sharedPref = getSharedPreferences("JustLive", Context.MODE_PRIVATE)
        val ignoreVersion = sharedPref.getInt("ignoreVersion", getVersionCode(context))
        checkUpdate(ignoreVersion, false)
        VideoViewManager.setConfig(
            VideoViewConfig.newBuilder()
            //使用ExoPlayer解码
            .setPlayerFactory(ExoMediaPlayerFactory.create())
            .build())
    }
}
