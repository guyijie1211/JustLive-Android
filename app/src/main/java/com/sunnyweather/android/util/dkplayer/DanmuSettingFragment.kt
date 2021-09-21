package com.sunnyweather.android.util.dkplayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.DanmuSetting
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_danmu_setting.*
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
            showAreaText.text = when (value) {
                0f -> "1/4屏"
                1f -> "半屏"
                2f -> "3/4屏"
                3f -> "全屏"
                else -> "???"
            }
        }
        alphaSlider.addOnChangeListener { _, value, _ ->
            alphaText.text = value.toInt().toString() + "%"
        }
        speedSlider.addOnChangeListener { _, value, _ ->
            speedText.text = when (value) {
                0f -> "慢"
                1f -> "适中"
                2f -> "快"
                3f -> "很快"
                else -> "???"
            }
        }
        sizeSlider.addOnChangeListener { _, value, _ ->
            sizeText.text = getNoMoreThanTwoDigits(value)
        }
        borderSlider.addOnChangeListener { _, value, _ ->
            borderText.text = getNoMoreThanTwoDigits(value)
        }
        miDuSlider.addOnChangeListener { _, value, _ ->
            miDuText.text = when (value) {
                0f -> "不过滤"
                1f -> "少量过滤"
                2f -> "减半"
                3f -> "强过滤"
                else -> "???"
            }
        }
        showAreaSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                setting.showArea = slider.value
                onDanmuSettingChangedListener.changeSetting(setting, "showArea")
            }
        })
        alphaSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                setting.alpha = slider.value
                onDanmuSettingChangedListener.changeSetting(setting, "alpha")
            }
        })
        speedSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                setting.speed = slider.value
                onDanmuSettingChangedListener.changeSetting(setting, "speed")
            }
        })
        sizeSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                setting.size = slider.value
                onDanmuSettingChangedListener.changeSetting(setting, "size")
            }
        })
        borderSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                setting.border = slider.value
                onDanmuSettingChangedListener.changeSetting(setting, "border")
            }
        })
        miDuSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                setting.miDu = slider.value
                onDanmuSettingChangedListener.changeSetting(setting, "miDu")
            }
        })
        showAreaSlider.value = setting.showArea
        alphaSlider.value = setting.alpha
        speedSlider.value = setting.speed
        sizeSlider.value = setting.size
        borderSlider.value = setting.border
        miDuSlider.value = setting.miDu
    }

    interface OnDanmuSettingChangedListener {
        fun getSetting(): DanmuSetting
        fun changeSetting(setting: DanmuSetting, updateItem: String)
    }

    fun getNoMoreThanTwoDigits(number: Float): String {
        val format = DecimalFormat("0.##")
        //未保留小数的舍弃规则，RoundingMode.FLOOR表示直接舍弃。
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }
}