package com.sunnyweather.android.ui.login

import android.os.Bundle
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
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.umeng.analytics.MobclickAgent

class LoginActivity: AppCompatActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val window: Window = this.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

            val decor: View = this.window.decorView
            decor.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            SunnyWeatherApplication.MIUISetStatusBarLightMode(this, true)
        }
        viewModel.loginResponseLiveDate.observe(this, { result ->
            val userInfo = result.getOrNull()
            if (userInfo is UserInfo) {
                MobclickAgent.onProfileSignIn(userInfo.userName)//友盟账号登录
                SunnyWeatherApplication.userInfo = userInfo
                SunnyWeatherApplication.isLogin.value = true
                //登录信息存本地
                SunnyWeatherApplication.saveLoginInfo(
                    this,
                    userInfo.userName,
                    SunnyWeatherApplication.encodeMD5(password_content.text.toString())
                )
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                onBackPressed()
            } else if (userInfo is String) {
                SunnyWeatherApplication.clearLoginInfo(this)
                Toast.makeText(this, userInfo, Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        //用户名校验
        userName_content.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                username_text.error = null
            }
        }
        //密码框校验
        password_content.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                password_text.error = null
            }
        }
        //登录按钮事件
        loginBtn.setOnClickListener {
            hideInput(this)
            if (checkAll()) {
                val userName = userName_content.text.toString()
                val password = SunnyWeatherApplication.encodeMD5(password_content.text.toString())
                viewModel.doLogin(userName, password)
            }
        }
        //注册按钮事件
        textButton.setOnClickListener {
            val intent = Intent(context, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    //提交注册前检验各个输入框是否正确
    private fun checkAll(): Boolean{
        if (!checkIfCorrect(username_text, userName_content)) return false
        if (!checkIfCorrect(password_text, password_content)) return false
        return true
    }
    //判断是否正确
    private fun checkIfCorrect(textInputLayout: TextInputLayout, textView: TextView): Boolean {
        var textInputLayoutCheck = true
        var textViewCheck = true
        if (textView.text.isEmpty()){
            textInputLayout.error = "不能为空"
            textViewCheck = false
        }
        if (textInputLayout.error != null){
            val note = "输入信息存在错误"
            note.showToast(context)
            textInputLayoutCheck = false
        }
        return textInputLayoutCheck && textViewCheck
    }
    /**
     * 关闭软键盘
     */
    private fun hideInput(activity: Activity) {
        if (activity.currentFocus != null) {
            val inputManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }
    //toast
    private fun String.showToast(context: Context) {
        Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
    }
}