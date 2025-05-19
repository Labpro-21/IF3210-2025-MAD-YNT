package com.ynt.purrytify.utils.downloadmanager

fun parseDurationToSeconds(duration: String): Int {
    val parts = duration.split(":")
    val minutes = parts[0].toIntOrNull() ?: 0
    val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val result = (minutes * 60 + seconds)*1000
    return result
}