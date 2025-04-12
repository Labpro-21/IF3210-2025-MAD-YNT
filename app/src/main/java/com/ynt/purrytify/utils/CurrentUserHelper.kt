package com.ynt.purrytify.utils

import android.content.Context

class CurrentUserHelper(context: Context) {
    private val sharedPref = context.getSharedPreferences("YNTUser", Context.MODE_PRIVATE)
    fun saveString(key: String, value: String) {
        sharedPref.edit().putString(key, value).apply()
    }
    fun getString(key: String, default: String = ""): String {
        return sharedPref.getString(key, default) ?: default
    }
}