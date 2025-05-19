package com.ynt.purrytify.utils.libraryscreenutils

import android.content.Context
import android.net.Uri
import java.io.File

fun deleteFileFromExternalStorage(context: Context, fileUri: Uri): Boolean {
    return try {
        val file = File(fileUri.path ?: return false)
        file.delete()
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}