package com.ynt.purrytify.ui.library.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.net.URLDecoder

fun getFileNameFromUri(context: Context, uri: Uri): String {
    var name = "unknown_file"
    val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && it.moveToFirst()) {
            name = it.getString(nameIndex)
        }
    }
    return name
}

fun getFileName(uri: Uri):String{
    val path = uri.toString().removePrefix("file://")
    val decodedPath = URLDecoder.decode(path, "UTF-8")
    val fileName = File(decodedPath).name
    return fileName
}


