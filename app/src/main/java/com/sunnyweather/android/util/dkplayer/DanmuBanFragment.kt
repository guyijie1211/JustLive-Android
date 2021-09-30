package com.sunnyweather.android.util.dkplayer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.UserInfo
import com.sunnyweather.android.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.fragment_danmu_banned.*
import kotlinx.android.synthetic.main.fragment_roomlist.*

class DanmuBanFragment: Fragment() {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_danmu_banned, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var banContents = ArrayList<String>()
        var isSelectedArray = ArrayList<String>()
        if (SunnyWeatherApplication.isLogin.value!!) {
            val banContentsString = SunnyWeatherApplication.userInfo!!.allContent
            val isSelectString = SunnyWeatherApplication.userInfo!!.selectedContent
            if (banContentsString != ""){
                if (banContentsString.contains(";")){
                    banContents = banContentsString.split(";") as ArrayList<String>
                } else {
                    banContents.add(banContentsString)
                }
            }
            if (isSelectString != ""){
                if (isSelectString.contains(";")) {
                    isSelectedArray = isSelectString.split(";") as ArrayList<String>
                } else {
                    isSelectedArray.add(isSelectString)
                }

            }
        }
        addClips(banContents, isSelectedArray)
        add_ban_btn.setOnClickListener {

        }
        viewModel.updateUserInfoLiveDate.observe(viewLifecycleOwner, { result ->
            val temp = result.getOrNull()
            if (temp is UserInfo) {
                SunnyWeatherApplication.userInfo = temp
                Toast.makeText(context, "屏蔽规则更新成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, temp.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        Log.i("test", "onPause")
    }

    //根据list生产clips
    private fun addClips(banArray: ArrayList<String>, isSelectedArray: ArrayList<String>) {
        for ((index, banContent) in banArray.withIndex()) {
            val chip = Chip(context)
            chip.id = 19981211 + index
            chip.isCloseIconVisible = true
            chip.text = banContent
            chip.isCheckable = true
            if (isSelectedArray.contains(banContent)) {
                chip.isChecked = true
            }
            //点击删除事件
            chip.setOnCloseIconClickListener {
                banArray.remove(banContent)
                if (chip.isChecked) {
                    isSelectedArray.remove(banContent)
                }
                ban_chipGroup.removeView(chip)
                saveBanInfo(banArray, isSelectedArray)
            }
            //选中事件
            chip.setOnCheckedChangeListener { chip, isChecked ->
                if (isChecked) {
                    isSelectedArray.add(chip.text.toString())
                } else {
                    isSelectedArray.remove(chip.text.toString())
                }
                saveBanInfo(banArray, isSelectedArray)
            }
            ban_chipGroup.addView(chip)
        }
    }

    //保存弹幕屏蔽信息
    private fun saveBanInfo(banArray: ArrayList<String>, isSelectedArray: ArrayList<String>) {
        if (!SunnyWeatherApplication.isLogin.value!!) {
            return
        }
        val userInfo = SunnyWeatherApplication.userInfo
        userInfo!!.allContent = banArray.joinToString(";")
        userInfo!!.selectedContent = isSelectedArray.joinToString(";")
        viewModel.changeUserInfo(userInfo)
    }
}