package com.sunnyweather.android.ui.login

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.sunnyweather.android.R
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.SunnyWeatherApplication.Companion.context
import com.sunnyweather.android.logic.model.UserInfo
import kotlinx.android.synthetic.main.activity_login.*
import android.app.Activity
import android.graphics.Color
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import java.util.*
import kotlin.concurrent.schedule

class LoginActivity: AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        bindProgressButton(loginBtn)
        viewModel.loginResponseLiveDate.observe(this, { result ->
            val userInfo: UserInfo = result.getOrNull() as UserInfo
            if (userInfo != null) {
                SunnyWeatherApplication.userInfo = userInfo
                SunnyWeatherApplication.isLogin = true
                Toast.makeText(context, "登陆成功", Toast.LENGTH_SHORT)
                Log.i("test", "${SunnyWeatherApplication.userInfo}---${SunnyWeatherApplication.isLogin} ")
                // Hide progress and show "Submit" text instead
                loginBtn.hideProgress(R.string.succeed)
                onBackPressed()
            } else {
                SunnyWeatherApplication.userInfo = null
                SunnyWeatherApplication.isLogin = false
                Toast.makeText(this, "登陆失败", Toast.LENGTH_SHORT)
                result.exceptionOrNull()?.printStackTrace()
            }
        })

        loginBtn.setOnClickListener {
            hideInput(this)
            val userName = userName_content.text.toString()
            val password = SunnyWeatherApplication.encodeMD5(password_content.text.toString())
            viewModel.doLogin(userName, password)
            // Show progress with "Loading" text
            loginBtn.showProgress {
                buttonTextRes = R.string.load_button
                progressColor = Color.WHITE
            }
        }
    }

    /**
     * 关闭软键盘
     */
    open fun hideInput(activity: Activity) {
        if (activity.currentFocus != null) {
            val inputManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }
}