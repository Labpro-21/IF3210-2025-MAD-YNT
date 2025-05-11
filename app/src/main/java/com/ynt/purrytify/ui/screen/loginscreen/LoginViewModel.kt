package com.ynt.purrytify.ui.screen.loginscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ynt.purrytify.models.ErrorResponse
import com.ynt.purrytify.models.LoginRequest
import com.ynt.purrytify.models.LoginResponse
import com.ynt.purrytify.network.RetrofitInstance
import com.ynt.purrytify.utils.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val data: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state = _state.asStateFlow()

    fun login(email: String, password: String, context: Context) {
        _state.value = LoginState.Loading
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    response.body()?.let {
                        TokenStorage(context).saveTokens(it.accessToken, it.refreshToken)
                        _state.value = LoginState.Success(it)
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val error = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    _state.value = LoginState.Error(error.error)
                }
            } catch (e: HttpException) {
                _state.value = LoginState.Error("Network error: ${e.message}")
            } catch (e: Exception) {
                _state.value = LoginState.Error("Unknown error: ${e.message}")
            }
        }
    }
}