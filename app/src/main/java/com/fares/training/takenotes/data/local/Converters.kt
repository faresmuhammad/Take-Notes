package com.fares.training.takenotes.data.local

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.room.TypeConverter
import com.fares.training.takenotes.utils.toBitmap
import com.fares.training.takenotes.utils.toByteArray
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun fromList(list: List<String>): String = Gson().toJson(list)

    @TypeConverter
    fun toList(string: String): List<String> =
        Gson().fromJson(string, object : TypeToken<List<String>>() {}.type)

    @TypeConverter
    fun toUri(value: String?): Uri? = value?.let {
        Uri.parse(it)
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String? = uri.toString()

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray = bitmap.toByteArray()

    @TypeConverter
    fun toBitmap(array: ByteArray): Bitmap = array.toBitmap()
}