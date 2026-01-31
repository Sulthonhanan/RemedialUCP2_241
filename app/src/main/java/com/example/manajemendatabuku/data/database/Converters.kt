package com.example.manajemendatabuku.data.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converters untuk Room Database
 * Mengkonversi Date ke Long dan sebaliknya
 */
class Converters {
    
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
