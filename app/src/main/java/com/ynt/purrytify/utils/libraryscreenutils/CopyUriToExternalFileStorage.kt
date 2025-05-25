package com.ynt.purrytify.ui.screen.libraryscreen.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

fun copyUriToExternalStorage(context: Context, uri: Uri, filename: String): Uri? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val outputDir = context.getExternalFilesDir("songs")
        if (inputStream != null && outputDir != null) {
            val outFile = File(outputDir, filename)
            FileOutputStream(outFile).use { output ->
                inputStream.copyTo(output)
            }
            Uri.fromFile(outFile)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
