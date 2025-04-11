package com.ynt.purrytify.utils

import android.content.Context

fun isUserAuthorized(context: Context): Boolean {
    val tokenStorage = TokenStorage(context)
    return !tokenStorage.getAccessToken().isNullOrBlank()
}