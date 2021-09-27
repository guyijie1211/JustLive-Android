package com.sunnyweather.android.ui.customerUIs

import android.app.Dialog
import android.content.Context
import android.text.Html
import android.widget.Toast
import com.allenliu.versionchecklib.v2.ui.UIActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.UpdateInfo
import kotlinx.android.synthetic.main.dialog_update.*

class UpdateDialog : Dialog{
    private var updateInfo: UpdateInfo

    constructor(context: Context, updateInfo: UpdateInfo) : super(context) {
        this.updateInfo = updateInfo
        initView()
    }

    private fun initView() {
        setContentView(R.layout.dialog_update)
        setCanceledOnTouchOutside(false)
        var descriptions = ""
        var index = 1
        for (item in updateInfo.description) {
            descriptions = "$descriptions$index.$item<br>"
            index++
        }
        update_description.text = Html.fromHtml("<div>$descriptions</div>")
        ignore_btn.setOnClickListener {
            var sharedPref = context.getSharedPreferences("JustLive", Context.MODE_PRIVATE)
            sharedPref.edit().putInt("ignoreVersion", updateInfo.versionNum).commit()
            Toast.makeText(context, "已忽略", Toast.LENGTH_SHORT).show()
            cancel()
        }
    }
}