package com.ynt.purrytify.utils.sharing

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set


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

fun CreateQRCode(songId: Int): Bitmap{
    val shareUrl = GetSharedURL(songId)
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(shareUrl, BarcodeFormat.QR_CODE, 1024, 1024)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap[x, y] =
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }

    return bitmap
}