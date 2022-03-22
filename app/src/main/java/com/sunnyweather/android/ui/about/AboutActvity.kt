package com.sunnyweather.android.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.ToastUtils
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.main_container
import java.lang.Exception

class AboutActvity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        var themeActived: Int
        val autoDark = sharedPreferences.getBoolean("autoDark", true)
        if (autoDark) {
            if(SunnyWeatherApplication.isNightMode(this)){
                themeActived = R.style.nightTheme
                sharedPreferences.edit().putInt("theme", themeActived).commit()
            } else {
                themeActived = R.style.SunnyWeather
                sharedPreferences.edit().putInt("theme", themeActived).commit()
            }
        } else {
            themeActived = sharedPreferences.getInt("theme", R.style.SunnyWeather)
        }
        setTheme(themeActived)
        setContentView(R.layout.activity_about)
        BarUtils.transparentStatusBar(this)
        BarUtils.addMarginTopEqualStatusBarHeight(about_container)
        if (themeActived != R.style.nightTheme) {
            BarUtils.setStatusBarLightMode(this, true)
            BarUtils.setStatusBarColor(this, resources.getColor(R.color.colorPrimaryVariant))
        } else {
            BarUtils.setStatusBarLightMode(this, false)
            BarUtils.setStatusBarColor(this, resources.getColor(R.color.colorPrimaryVariant_night))
        }

        about_version.text = AppUtils.getAppVersionName()

        about_pic.setImageDrawable(AppUtils.getAppIcon())

        about_kuan.setOnClickListener {
            try {
                val intent = Intent()
                intent.setClassName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity");
                intent.action = "android.intent.action.VIEW";
                intent.data = Uri.parse("coolmarket://u/645623")
                startActivity(intent)
            } catch (e:Exception) {
                ToastUtils.showShort("启动失败")
            }
        }
        about_weibo.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse("sinaweibo://userinfo?uid=5211151565")
            startActivity(intent)
        }
        about_qq.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://qm.qq.com/cgi-bin/qm/qr?k=_2KootdkU0ikLiFhBCQMJKW7PjHzySZ8&authKey=2dGp04G04G/+a1KHiIjqjpg1Se+/TgpQ5yzpEbMkzP9Y6lkrFdReKdtBtg6xC+Cs&noverify=0"))
            this.startActivity(intent)
        }
        about_telegram.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+4r0VneskqvY1NWNl"))
            this.startActivity(intent)
        }
        about_github.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/guyijie1211"))
            this.startActivity(intent)
        }
        about_web.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://live.yj1211.work"))
            this.startActivity(intent)
        }
        about_back.setOnClickListener {
            this.onBackPressed()
        }
        about_email.setOnClickListener {
            val email = Intent(Intent.ACTION_SEND)
            email.type = "text/plain"
            email.putExtra(Intent.EXTRA_EMAIL, arrayOf("1056025931@qq.com"))
            startActivity(Intent.createChooser(email, "选择邮件APP"))
        }
//        about_apple.setOnClickListener {
//            ToastUtils.showShort("开发中")
//        }
    }
}