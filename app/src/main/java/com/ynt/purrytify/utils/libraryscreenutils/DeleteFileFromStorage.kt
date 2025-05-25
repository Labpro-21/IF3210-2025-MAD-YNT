package com.ynt.purrytify.utils.libraryscreenutils

import android.net.Uri
import java.io.File

fun deleteFileFromStorage(uri: Uri): Boolean {
    return try {
        val file = File(uri.path ?: return false)
        if (file.exists()) {
            file.delete()
        } else {
            false
        }
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}