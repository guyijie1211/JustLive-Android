package com.sunnyweather.android.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.blankj.utilcode.util.BarUtils
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.SunnyWeatherApplication.Companion.context
import kotlinx.android.synthetic.main.activity_liveroom.*
import kotlinx.android.synthetic.main.activity_liveroom.player_container
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        var theme: Int
        val autoDark = sharedPreferences.getBoolean("autoDark", true)
        val pureDark = sharedPreferences.getBoolean("pureDark", false)
        if (autoDark) {
            if(SunnyWeatherApplication.isNightMode(this)){
                theme = R.style.nightTheme
                sharedPreferences.edit().putInt("theme", theme).commit()
            } else {
                theme = R.style.SunnyWeather
                sharedPreferences.edit().putInt("theme", theme).commit()
            }
        } else {
            theme = sharedPreferences.getInt("theme", R.style.SunnyWeather)
        }
        theme = if (theme == R.style.SunnyWeather) {
            R.style.PreferenceScreen
        } else if (pureDark) {
            R.style.PreferenceScreen_dark
        } else {
            R.style.PreferenceScreen_night
        }
        setTheme(theme)
        setContentView(R.layout.activity_setting)
        BarUtils.transparentStatusBar(this)
        BarUtils.addMarginTopEqualStatusBarHeight(setting_toolbar)
        if (theme != R.style.PreferenceScreen_night) {
            BarUtils.setStatusBarLightMode(this, true)
        } else {
            BarUtils.setStatusBarLightMode(this, false)
        }
    }

}