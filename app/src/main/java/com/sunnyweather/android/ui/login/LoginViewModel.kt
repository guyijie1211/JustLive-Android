package com.sunnyweather.android.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository

class LoginViewModel : ViewModel() {
    data class LoginRequest(val username: String, val password: String)

    private val loginLiveData = MutableLiveData<LoginRequest>()

    val loginResponseLiveDate = Transformations.switchMap(loginLiveData) {
            value -> Repository.login(value.username, value.password)
    }

    fun doLogin(username: String, password: String) {
        loginLiveData.value = LoginRequest(username, password)
    }
}