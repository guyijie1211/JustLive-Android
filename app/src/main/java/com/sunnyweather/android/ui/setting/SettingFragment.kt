package com.sunnyweather.android.ui.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.app.ActivityCompat.recreate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.sunnyweather.android.R

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
        val signaturePreference: SwitchPreferenceCompat? = findPreference("dayNight")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        signaturePreference?.isChecked =
            sharedPreferences.getInt("theme", R.style.SunnyWeather) != R.style.SunnyWeather
        signaturePreference?.setOnPreferenceChangeListener { _, newValue  ->
            if (newValue as Boolean) {
                sharedPreferences.edit().putInt("theme", R.style.nightTheme).commit()
                signaturePreference.isChecked = true
                activity?.recreate()
            } else {
                sharedPreferences.edit().putInt("theme", R.style.SunnyWeather).commit()
                signaturePreference.isChecked = false
                activity?.recreate()
            }
            true
        }
    }
}