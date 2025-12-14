package com.sarangem.zenwell.database.typeconverters

import androidx.room.TypeConverter

class WeekdaysListConverter {

    @TypeConverter
    fun fromWeekdaysList(value: List<Int>): String {
        return value.joinToString(",") { it.toString() }
    }

    @TypeConverter
    fun toWeekdaysList(value: String): List<Int> {
        return if(value.isBlank()){
            emptyList()
        } else {
            value.split(",").map { it.toInt() }
        }
    }
}