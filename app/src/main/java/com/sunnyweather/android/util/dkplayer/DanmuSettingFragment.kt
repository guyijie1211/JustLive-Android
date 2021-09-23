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
        merge_select.isSelected = setting.merge
        bold_select.isSelected = setting.bold
        fps_select.isSelected = setting.fps
    }

    interface OnDanmuSettingChangedListener {
        fun getSetting(): DanmuSetting
        fun changeSetting(setting: DanmuSetting, updateItem: String)
    }

    private fun getNoMoreThanTwoDigits(number: Float): String {
        val format = DecimalFormat("0.##")
        //未保留小数的舍弃规则，RoundingMode.FLOOR表示直接舍弃。
        format.roundingMode = RoundingMode.FLOOR
        return format.format(number)
    }
}