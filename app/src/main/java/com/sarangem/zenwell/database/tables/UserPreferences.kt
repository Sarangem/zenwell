/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_prefs")
data class UserPreferences(
    @PrimaryKey val id: Int = 1,
    val showNotificationPermissionCard: Boolean = true,

    // null: old user
    // 1: first entry to app
    // 2: second entry to app
    val firstEntry: Int? = null
)