package com.ynt.purrytify.utils

import android.content.Context
import android.content.Intent
import com.ynt.purrytify.MainActivity
import com.ynt.purrytify.models.RefreshTokenRequest
import com.ynt.purrytify.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class SessionManager(private val context: Context) {
    val tokenStorage = TokenStorage(context)

    fun getAccessToken(): String? = tokenStorage.getAccessToken()
    fun getRefreshToken(): String? = tokenStorage.getRefreshToken()

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    fun logout() {
        tokenStorage.clearTokens()
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }

    suspend fun isTokenValid(): Boolean {
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

    suspend fun refreshToken(): Boolean {
        val refreshToken = getRefreshToken() ?: return false
        return withContext(Dispatchers.IO) {
            try {
                val refreshTokenRequest = RefreshTokenRequest(refreshToken)
                val response = RetrofitInstance.api.refreshToken(refreshTokenRequest)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.accessToken.isNullOrBlank()) {
                        tokenStorage.saveTokens(body.accessToken, body.refreshToken)
                        true
                    } else false
                } else false
            } catch (e: Exception) {
                false
            }
        }
    }

    fun isTokenExpired(): Boolean {
        val token = getAccessToken() ?: return true
        return isJwtExpired(token)
    }

    suspend fun refreshExpired(): Boolean {
        if (isTokenExpired()) {
            val success = refreshToken()
            if (!success) {
                logout()
                return false
            }
        }
        return true
    }

}
