package com.sunnyweather.android.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.BarUtils
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication.Companion.context
import kotlinx.android.synthetic.main.activity_liveroom.*
import kotlinx.android.synthetic.main.activity_liveroom.player_container
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var theme = sharedPreferences.getInt("theme", R.style.SunnyWeather)
        setTheme(theme)
        setContentView(R.layout.activity_setting)
        BarUtils.transparentStatusBar(this)
        BarUtils.addMarginTopEqualStatusBarHeight(setting_toolbar)
        if (theme != R.style.nightTheme) {
            BarUtils.setStatusBarLightMode(this, true)
        } else {
            BarUtils.setStatusBarLightMode(this, false)
        }
    }

}