package com.ynt.purrytify.utils

import android.content.Context
import android.content.Intent
import com.ynt.purrytify.MainActivity

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