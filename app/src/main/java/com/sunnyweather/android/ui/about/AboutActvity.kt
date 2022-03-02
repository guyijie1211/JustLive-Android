package com.sunnyweather.android.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
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

        about_kuan.setOnClickListener {
            ToastUtils.showShort("ppp")
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.coolapk.com/u/645623"))
            this.startActivity(intent)
        }
    }
}