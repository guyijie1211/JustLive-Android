package com.sunnyweather.android.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.app.ActivityCompat.recreate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.sunnyweather.android.R

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
        val signaturePreference: SwitchPreferenceCompat? = findPreference("dayNight")
//        signaturePreference?.setOnPreferenceChangeListener { _, newValue  ->
//            if (newValue as Boolean) {
//                 context?.setTheme(R.style.nightTheme)
//                sharedPreferences.edit().putInt("theme", theme).commit()
//
//            } else {
//                context?.setTheme(R.style.SunnyWeather)
//            }
//            true
//        }
    }
}