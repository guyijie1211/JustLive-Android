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
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.main_container

class AboutActvity: AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val themeActived = sharedPreferences.getInt("theme", R.style.SunnyWeather)
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

        about_pic.setImageDrawable(AppUtils.getAppIcon())

        about_kuan.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/u/645623"))
            this.startActivity(intent)
        }
        about_weibo.setOnClickListener {
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://weibo.com/u/5211151565"))
            this.startActivity(intent)
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
        about_apple.setOnClickListener {
            ToastUtils.showShort("开发中")
        }
    }
}