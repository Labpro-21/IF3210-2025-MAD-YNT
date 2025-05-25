package com.ynt.purrytify.utils.sharing

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

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

fun CreateQRCode(songId: Int, songTitle:String, songArtist:String): Bitmap{
    val shareUrl = GetSharedURL(songId)
    val qrSize = 1024
    val bitMatrix: BitMatrix = MultiFormatWriter().encode(
        shareUrl,
        BarcodeFormat.QR_CODE,
        qrSize,
        qrSize
    )

    val qrBitmap = createBitmap(qrSize, qrSize, Bitmap.Config.RGB_565)

    for (x in 0 until qrSize) {
        for (y in 0 until qrSize) {
            qrBitmap[x, y] =
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        }
    }

    val padding = 48
    val textHeight = 200
    val finalHeight = qrSize + textHeight + padding

    val finalBitmap = Bitmap.createBitmap(qrSize, finalHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(finalBitmap)
    canvas.drawColor(android.graphics.Color.WHITE)

    val paint = Paint().apply {
        color = android.graphics.Color.BLACK
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    paint.textSize = 100f
    canvas.drawText(songTitle, qrSize / 2f, 80f, paint)
    paint.textSize = 80f
    canvas.drawText(songArtist, qrSize / 2f, 200f, paint)
    canvas.drawBitmap(qrBitmap, 0f, textHeight.toFloat(), null)

    return finalBitmap
}

fun ShareQRCode(context: Context, bitmap: Bitmap){
    val cachePath = File(context.cacheDir, "shared_qr")
    cachePath.mkdirs()
    val file = File(cachePath, "purritifyQR.png")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }

    val contentUri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, contentUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
}