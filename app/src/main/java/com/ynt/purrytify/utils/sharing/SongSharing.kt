package com.ynt.purrytify.utils.sharing

import android.content.Context
import android.content.Intent


fun GetSharedURL(songId: Int): String {
    return "purrytify://song/$songId"
}

fun ShareViaURL(context: Context, songId: Int){

    val shareUrl = GetSharedURL(songId)
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareUrl)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, "Share Song")
    context.startActivity(shareIntent)
}