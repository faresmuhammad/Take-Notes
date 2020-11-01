package com.fares.training.takenotes.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

fun Bitmap.toByteArray(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)

fun Uri.toByteArray(context: Context): ByteArray? =
    context.contentResolver.openInputStream(this)?.buffered()?.use { it.readBytes() }

fun String.toByteArray(): ByteArray = Base64.decode(this, Base64.DEFAULT)

fun ByteArray.toBase64String(): String = Base64.encodeToString(this, Base64.DEFAULT)