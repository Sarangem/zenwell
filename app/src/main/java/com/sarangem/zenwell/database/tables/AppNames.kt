/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database.tables

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "app_names",
    indices = [ Index(value = ["packageName", "viewId"], unique = true) ]
)
data class AppNames (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // eg: "com.android.chrome"
    val packageName: String,

    // if empty, meaning the whole app is blocked, not the specific screen
    // eg: "com.android.chrome:id/url_bar"
    val viewId: String? = null,

    // eg: to block youtube on chrome "youtube.com"
    val viewKeyword: String? = null,

    // display name for app UI only
    val viewTitle: String? = null
)