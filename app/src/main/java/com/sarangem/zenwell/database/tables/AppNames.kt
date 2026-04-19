/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "app_names",
    indices = [ Index(value = ["title"], unique = true) ]
)
data class AppNames (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // contains packageName or viewID
    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "view_title")
    val viewTitle: String? = null
)