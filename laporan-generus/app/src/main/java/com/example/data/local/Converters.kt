package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.CategoryData
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listMyType = Types.newParameterizedType(List::class.java, CategoryData::class.java)
    private val adapter = moshi.adapter<List<CategoryData>>(listMyType)

    @TypeConverter
    fun fromCategoryList(value: List<CategoryData>?): String? {
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toCategoryList(value: String?): List<CategoryData>? {
        return value?.let { adapter.fromJson(it) }
    }
}
