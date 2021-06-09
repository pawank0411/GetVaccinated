package com.vaccine.slot.notifier.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromList(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(string: String): ArrayList<String> {
        return Gson().fromJson(string, object : TypeToken<List<String>>() {}.type)
    }
}