package com.smartgrocery.pantry.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

object Converters {
    private val json = Json

    @TypeConverter
    @JvmStatic
    fun fromStringList(value: List<String>?): String? = value?.let { json.encodeToString(ListSerializer(String.serializer()), it) }

    @TypeConverter
    @JvmStatic
    fun toStringList(value: String?): List<String>? = value?.let { json.decodeFromString(ListSerializer(String.serializer()), it) }
}

