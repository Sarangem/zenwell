/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database.migrations

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

        db.execSQL(
            """
            INSERT INTO `app_names_new` (`id`, `packageName`, `viewId`, `viewKeyword`, `viewTitle`)
            SELECT 
                `id`,
                CASE 
                    WHEN instr(`title`, ':id/') > 0 THEN substr(`title`, 1, instr(`title`, ':id/') - 1)
                    ELSE `title`
                END,
                CASE 
                    WHEN instr(`title`, ':id/') > 0 THEN substr(`title`, instr(`title`, ':id/') + 4)
                    ELSE NULL
                END,
                NULL,
                `viewTitle`
            FROM `app_names`
            """.trimIndent()
        )

        db.execSQL("DROP TABLE `app_names`")
        db.execSQL("ALTER TABLE `app_names_new` RENAME TO `app_names`")

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_app_names_packageName_viewId_viewKeyword` ON `app_names` (`packageName`, `viewId`, `viewKeyword`)")
    }
}