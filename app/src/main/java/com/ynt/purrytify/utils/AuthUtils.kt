package com.ynt.purrytify.utils

import android.content.Context
import android.content.Intent

import com.ynt.purrytify.MainActivity
import com.ynt.purrytify.data.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

fun isUserAuthorized(context: Context): Boolean {
    val tokenStorage = TokenStorage(context)
    return !tokenStorage.getAccessToken().isNullOrBlank()
}

fun logout(context: Context){
    val tokenStorage = TokenStorage(context)
    tokenStorage.clearTokens()
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
}

suspend fun isTokenValid(context: Context): Boolean {
    val accessToken = TokenStorage(context).getAccessToken() ?: return false
    return withContext(Dispatchers.IO) {
        try {
            val response = RetrofitInstance.api.verifyToken("Bearer $accessToken")
            response.isSuccessful
        } catch (e: HttpException) {
            if (e.code() == 401) {
                false
            } else {
                true
            }
        } catch (e: Exception) {
            true
        }
    }
}