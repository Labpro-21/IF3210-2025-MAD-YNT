package com.ynt.purrytify.utils

import android.util.Base64
import org.json.JSONObject

fun isJwtExpired(token: String): Boolean {
    val parts = token.split(".")
    if (parts.size != 3) {
        return true
    }
    val payload = parts[1]
    val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
    val decodedString = String(decodedBytes)
    try {
        val jsonObject = JSONObject(decodedString)
        val expirationTime = jsonObject.getLong("exp") * 1000
        val currentTime = System.currentTimeMillis()
        return currentTime > expirationTime
    } catch (e: Exception) {
        return true
    }
}
