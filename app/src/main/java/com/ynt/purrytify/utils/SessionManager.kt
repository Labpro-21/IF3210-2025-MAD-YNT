package com.ynt.purrytify.utils

import android.content.Context
import android.content.Intent
import com.ynt.purrytify.MainActivity
import com.ynt.purrytify.models.RefreshTokenRequest
import com.ynt.purrytify.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import androidx.core.content.edit

class SessionManager(private val context: Context) {
    private val USER_CODE = "YNTUser"
    private val user = context.getSharedPreferences(USER_CODE, Context.MODE_PRIVATE)
    private val tokenStorage = TokenStorage(context)

    fun setUser(value: String) {
        user.edit { putString(USER_CODE, value) }
    }

    fun getUser(default: String = ""): String {
        return user.getString(USER_CODE, default) ?: default
    }

    fun clearUser(){
        user.edit { clear() }
    }

    private fun getAccessToken(): String? = tokenStorage.getAccessToken()
    private fun getRefreshToken(): String? = tokenStorage.getRefreshToken()

    fun saveTokens(accessToken: String,refreshToken: String){
        tokenStorage.saveTokens(accessToken,refreshToken)
    }

    fun clearTokens(){
        tokenStorage.clearTokens()
    }

    suspend fun isLoggedIn(): Boolean = (!getAccessToken().isNullOrBlank() && isTokenValid())

    private fun logout() {
        clearTokens()
        clearUser()
    }

    private suspend fun isTokenValid(): Boolean {
        val accessToken = getAccessToken() ?: return false
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.verifyToken("Bearer $accessToken")
                response.isSuccessful
            } catch (e: HttpException) {
                e.code() != 403
            } catch (e: Exception) {
                true
            }
        }
    }

    private suspend fun refreshToken(): Boolean {
        val refreshToken = getRefreshToken() ?: return false
        return withContext(Dispatchers.IO) {
            try {
                val refreshTokenRequest = RefreshTokenRequest(refreshToken)
                val response = RetrofitInstance.api.refreshToken(refreshTokenRequest)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.accessToken.isNotBlank()) {
                        tokenStorage.saveTokens(body.accessToken, body.refreshToken)
                        true
                    } else false
                } else false
            } catch (e: Exception) {
                false
            }
        }
    }

    private fun isTokenExpired(): Boolean {
        val token = getAccessToken() ?: return true
        return isJwtExpired(token)
    }

    suspend fun refreshExpired(): Boolean {
        val success = refreshToken()
        if (!success) {
            logout()
            return false
        }
        return true
    }
}
