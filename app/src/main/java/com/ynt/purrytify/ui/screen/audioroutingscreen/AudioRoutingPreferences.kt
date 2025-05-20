package com.ynt.purrytify.ui.screen.audioroutingscreen

import android.content.Context
import android.content.SharedPreferences

private const val PREFS_NAME = "audio_routing_prefs"
private const val SELECTED_DEVICE_ID_KEY = "selected_audio_device_id"

fun saveSelectedAudioDeviceId(context: Context, deviceId: Int) {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().putInt(SELECTED_DEVICE_ID_KEY, deviceId).apply()
}

fun getSelectedAudioDeviceId(context: Context): Int? {
    val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return if (prefs.contains(SELECTED_DEVICE_ID_KEY)) prefs.getInt(SELECTED_DEVICE_ID_KEY, -1)
        .takeIf { it != -1 } else null

}

