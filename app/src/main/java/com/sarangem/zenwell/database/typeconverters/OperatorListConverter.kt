/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database.typeconverters

import androidx.room.TypeConverter
import com.sarangem.zenwell.model.MathOperators

class OperatorListConverter {

    @TypeConverter
    fun fromOperatorList(value: List<MathOperators>): String {
        return value.joinToString(",") { it.name }
    }

    @TypeConverter
    fun toOperatorList(value: String): List<MathOperators> {
        return if(value.isBlank()){
            emptyList()
        } else {
            value.split(",").map { MathOperators.valueOf(it) }
        }
    }
}