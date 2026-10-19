/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database.migrations

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.core.database.getStringOrNull
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_21_22 = object : Migration(21, 22) {
    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `app_names_new` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `packageName` TEXT NOT NULL,
                `viewId` TEXT,
                `viewKeyword` TEXT,
                `viewTitle` TEXT
            )
            """.trimIndent()
        )

        val oldTableContents = db.query("SELECT `id`, `title`, `view_title` FROM `app_names`")
        oldTableContents.use { row ->
            while (row.moveToNext()) {
                val title = row.getString(1)
                db.insert(
                    "app_names_new",
                    SQLiteDatabase.CONFLICT_REPLACE,
                    contentValuesOf(
                        "id" to row.getInt(0),
                        "packageName" to title.substringBefore(":id/"),
                        "viewId" to title,
                        "viewKeyword" to null,
                        "viewTitle" to row.getStringOrNull(2)
                    )
                )
            }
        }

        db.execSQL("DROP TABLE `app_names`")
        db.execSQL("ALTER TABLE `app_names_new` RENAME TO `app_names`")

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_app_names_packageName_viewId_viewKeyword` ON `app_names` (`packageName`, `viewId`, `viewKeyword`)")
    }
}