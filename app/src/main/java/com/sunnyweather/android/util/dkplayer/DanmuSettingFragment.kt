package com.sunnyweather.android.util.dkplayer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
//import com.efs.sdk.base.newsharedpreferences.SharedPreferencesUtils.getSharedPreferences
import com.google.android.material.slider.Slider
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.DanmuSetting
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_danmu_setting.*
import xyz.doikki.videoplayer.player.VideoView
import java.math.RoundingMode
import java.text.DecimalFormat

class DanmuSettingFragment : Fragment() {
    private lateinit var onDanmuSettingChangedListener: OnDanmuSettingChangedListener
    private lateinit var setting: DanmuSetting
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_danmu_setting, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        onDanmuSettingChangedListener = context as OnDanmuSettingChangedListener
        setting = onDanmuSettingChangedListener.getSetting()
        init()
    }

    private fun init() {
        showAreaSlider.addOnChangeListener { _, value, _ ->
            if (value == 20f) {
                showAreaText.text = "不限制"
            } else {
                showAreaText.text = value.toInt().toString() + "行"
            }
            setting.showArea = value
            onDanmuSettingChangedListener.changeSetting(setting, "showArea")
        }
        alphaSlider.addOnChangeListener { _, value, _ ->
            alphaText.text = (value * 100).toInt().toString() + "%"
            setting.alpha = value
            onDanmuSettingChangedListener.changeSetting(setting, "alpha")
        }
        speedSlider.addOnChangeListener { _, value, _ ->
            speedText.text = (value * 100 / 4).toInt().toString() + "%"
            setting.speed = value
            onDanmuSettingChangedListener.changeSetting(setting, "speed")
        }
        sizeSlider.addOnChangeListener { _, value, _ ->
            sizeText.text = getNoMoreThanTwoDigits(value)
            setting.size = value
            onDanmuSettingChangedListener.changeSetting(setting, "size")
        }
        borderSlider.addOnChangeListener { _, value, _ ->
            borderText.text = getNoMoreThanTwoDigits(value)
            setting.border = value
            onDanmuSettingChangedListener.changeSetting(setting, "border")
        }
        merge_select.setOnCheckedChangeListener { _, isChecked ->
            setting.merge = isChecked
            onDanmuSettingChangedListener.changeSetting(setting, "merge")
        }
        bold_select.setOnCheckedChangeListener { _, isChecked ->
            setting.bold = isChecked
            onDanmuSettingChangedListener.changeSetting(setting, "bold")
        }
        fps_select.setOnCheckedChangeListener { _, isChecked ->
            setting.fps = isChecked
            onDanmuSettingChangedListener.changeSetting(setting, "fps")
        }
        showAreaSlider.value = setting.showArea
        alphaSlider.value = setting.alpha
        speedSlider.value = setting.speed
        sizeSlider.value = setting.size
        borderSlider.value = setting.border
        merge_select.isChecked = setting.merge
        bold_select.isChecked = setting.bold
        fps_select.isChecked = setting.fps

        var sharedPref = requireContext().getSharedPreferences("JustLive", Context.MODE_PRIVATE)

        when (sharedPref.getInt("playerSize", R.id.radio_button_1)) {
            R.id.radio_button_1 -> {
                radio_button_1.isChecked = true
                onDanmuSettingChangedListener.changeVideoSize(VideoView.SCREEN_SCALE_DEFAULT)
            }
            R.id.radio_button_2 -> {
                radio_button_2.isChecked = true
                onDanmuSettingChangedListener.changeVideoSize(VideoView.SCREEN_SCALE_MATCH_PARENT)
            }
            R.id.radio_button_3 -> {
                radio_button_3.isChecked = true
                onDanmuSettingChangedListener.changeVideoSize(VideoView.SCREEN_SCALE_CENTER_CROP)
            }
        }
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_button_1 -> {
                    onDanmuSettingChangedListener.changeVideoSize(VideoView.SCREEN_SCALE_DEFAULT)
                }
                R.id.radio_button_2 -> {
                    onDanmuSettingChangedListener.changeVideoSize(VideoView.SCREEN_SCALE_MATCH_PARENT)
                }
                R.id.radio_button_3 -> {
                    onDanmuSettingChangedListener.changeVideoSize(VideoView.SCREEN_SCALE_CENTER_CROP)
                }
            }

            sharedPref.edit().putInt("playerSize", checkedId).commit()
        }
    }

    interface OnDanmuSettingChangedListener {
        fun getSetting(): DanmuSetting
        fun changeSetting(setting: DanmuSetting, updateItem: String)
        fun changeVideoSize(size: Int)
    }

    private fun getNoMoreThanTwoDigits(number: Float): String {
        val format = DecimalFormat("0.##")
        //未保留小数的舍弃规则，RoundingMode.FLOOR表示直接舍弃。
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }
}