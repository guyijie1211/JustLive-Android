package com.sunnyweather.android.ui.setting

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ToastUtils
import com.efs.sdk.base.newsharedpreferences.SharedPreferencesUtils.getSharedPreferences
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.UpdateInfo
import com.sunnyweather.android.ui.about.AboutActvity
import com.sunnyweather.android.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.dialog_donate.*
import kotlinx.android.synthetic.main.dialog_update.*
import moe.feng.alipay.zerosdk.AlipayZeroSdk

class SettingFragment : PreferenceFragmentCompat() {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting, rootKey)
        val signaturePreference: SwitchPreferenceCompat? = findPreference("dayNight")
        val autoDark: SwitchPreferenceCompat? = findPreference("autoDark")
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val versionPreferences = findPreference<Preference>("version")
        val aboutPreferences = findPreference<Preference>("about_activity")
        val donatePreferences = findPreference<Preference>("donate")
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
        autoDark?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                if(SunnyWeatherApplication.isNightMode(requireActivity())){
                    sharedPreferences.edit().putInt("theme", R.style.nightTheme).commit()
                    signaturePreference?.isChecked = true
                    activity?.recreate()
                } else {
                    sharedPreferences.edit().putInt("theme", R.style.SunnyWeather).commit()
                    signaturePreference?.isChecked = false
                    activity?.recreate()
                }
            }
            true
        }
        signaturePreference?.setOnPreferenceClickListener {
            sharedPreferences.edit().putBoolean("autoDark", false).commit()
        }

        versionPreferences?.summary = "当前版本:" + AppUtils.getAppVersionName()
        versionPreferences?.setOnPreferenceClickListener {
            // do something
            viewModel.checkVersion()
            true
        }
        aboutPreferences?.setOnPreferenceClickListener {
            // 打开关于页面
            val intent = Intent(requireContext(), AboutActvity::class.java)
            requireContext().startActivity(intent)
            true
        }
        donatePreferences?.setOnPreferenceClickListener {
            MaterialDialog(requireContext()).show {
                customView(R.layout.dialog_donate)
                var payPlatform = "zfb"
                donate_cancel.setOnClickListener {
                    dismiss()
                }
                donate_save_cancel.setOnClickListener {
                    donate_contain.visibility = View.VISIBLE
                    donate_pic_contain.visibility = View.GONE
                }
                donate_zfb.setOnClickListener {
                    if (AlipayZeroSdk.hasInstalledAlipayClient(context)) {
                        AlipayZeroSdk.startAlipayClient(activity, "fkx19479mrxqi6tzhgw0bd0")
                    } else {
                        payPlatform = "zfb"
                        donate_pic.setImageDrawable(resources.getDrawable(R.drawable.zfb_pic))
                        donate_contain.visibility = View.GONE
                        donate_pic_contain.visibility = View.VISIBLE
                        donate_save_zfb.visibility = View.VISIBLE
                        donate_save_wx.visibility = View.INVISIBLE
                    }
                }
                donate_wx.setOnClickListener {
                    payPlatform = "wx"
                    donate_pic.setImageDrawable(resources.getDrawable(R.drawable.wx_pic))
                    donate_contain.visibility = View.GONE
                    donate_pic_contain.visibility = View.VISIBLE
                    donate_save_zfb.visibility = View.INVISIBLE
                    donate_save_wx.visibility = View.VISIBLE
                }
                donate_save_zfb.setOnClickListener {
                    ImageUtils.save2Album(
                        ImageUtils.drawable2Bitmap(resources.getDrawable(R.drawable.zfb_pic)),
                        Bitmap.CompressFormat.JPEG)
                    ToastUtils.showLong("二维码已保存到相册,请打开支付宝扫码使用")
                }
                donate_save_wx.setOnClickListener {
                    ImageUtils.save2Album(
                        ImageUtils.drawable2Bitmap(resources.getDrawable(R.drawable.wx_pic)),
                        Bitmap.CompressFormat.JPEG)
                    ToastUtils.showLong("二维码已保存到相册,请打开微信扫码使用")
                }
            }
            true
        }

        viewModel.updateResponseLiveData.observe(this, { result ->
            val updateInfo = result.getOrNull()
            if (updateInfo is UpdateInfo) {
                var sharedPref = getSharedPreferences(requireContext(), "JustLive")
                val ignoreVersion = sharedPref.getInt("ignoreVersion",0)
                val versionNum = SunnyWeatherApplication.getVersionCode(SunnyWeatherApplication.context)
                if (versionNum == updateInfo.versionNum) {
                    Toast.makeText(SunnyWeatherApplication.context, "当前已是最新版本^_^", Toast.LENGTH_SHORT).show()
                    return@observe
                }
                var descriptions = ""
                var index = 1
                for (item in updateInfo.description) {
                    descriptions = "$descriptions$index.$item<br>"
                    index++
                }
                val dialogContent = Html.fromHtml("<div>$descriptions</div>")
                MaterialDialog(requireContext()).show {
                    customView(R.layout.dialog_update)
                    update_description.text = dialogContent
                    update_version.text = "版本: ${updateInfo.latestVersion}"
                    update_size.text = "下载体积: ${updateInfo.apkSize}"
                    versionchecklib_version_dialog_cancel.setOnClickListener {
                        dismiss()
                    }
                    versionchecklib_version_dialog_commit.setOnClickListener {
                        val uri = Uri.parse(updateInfo.updateUrl)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.addCategory(Intent. CATEGORY_BROWSABLE)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    ignore_btn.visibility = View.GONE

                }
            } else if(updateInfo is String){
                Toast.makeText(requireContext(), "用户密码已修改，请重新登录", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}