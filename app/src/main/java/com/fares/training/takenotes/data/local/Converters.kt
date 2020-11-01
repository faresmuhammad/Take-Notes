package com.fares.training.takenotes.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {



    @TypeConverter
    fun fromStringNullableList(list: List<String>?): String? = Gson().toJson(list)

    @TypeConverter
    fun toStringNullableList(string: String?): List<String>? =
        Gson().fromJson(string, object : TypeToken<List<String>>() {}.type)



}