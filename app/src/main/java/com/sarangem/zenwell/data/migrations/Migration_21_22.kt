/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.data.migrations

import android.database.sqlite.SQLiteDatabase
import androidx.core.content.contentValuesOf
import androidx.core.database.getStringOrNull
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_21_22 = object : Migration(21, 22) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // Migrate app_names table

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `blocked_apps_backup` (
                `id` INTEGER PRIMARY KEY NOT NULL,
                `schedule_id` INTEGER NOT NULL,
                `app_id` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO `blocked_apps_backup` (`id`, `schedule_id`, `app_id`)
            SELECT `id`, `schedule_id`, `app_id` FROM `blocked_apps`
            """.trimIndent()
        )

        db.execSQL("DROP TABLE `blocked_apps`")

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
                        "viewId" to if (title.contains(":id/")) title else null,
                        "viewKeyword" to null,
                        "viewTitle" to row.getStringOrNull(2)
                    )
                )
            }
        }

        db.execSQL("DROP TABLE `app_names`")
        db.execSQL("ALTER TABLE `app_names_new` RENAME TO `app_names`")

        db.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS `index_app_names_packageName_viewId_viewKeyword`
                ON `app_names` (`packageName`, `viewId`, `viewKeyword`)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `blocked_apps` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `schedule_id` INTEGER NOT NULL,
                `app_id` INTEGER NOT NULL,
                FOREIGN KEY(`schedule_id`) REFERENCES `schedules`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`app_id`) REFERENCES `app_names`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            INSERT INTO `blocked_apps` (`id`, `schedule_id`, `app_id`)
            SELECT `id`, `schedule_id`, `app_id` FROM `blocked_apps_backup`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `blocked_apps_backup`")

        db.execSQL("CREATE INDEX IF NOT EXISTS `index_blocked_apps_schedule_id` ON `blocked_apps` (`schedule_id`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_blocked_apps_app_id` ON `blocked_apps` (`app_id`)")

        // Migrate user_preferences table

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `user_prefs_new` (
                `id` INTEGER NOT NULL PRIMARY KEY,
                `firstEntry` INTEGER NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            INSERT INTO `user_prefs_new` (`id`, `firstEntry`)
            SELECT `id`, `firstEntry` FROM `user_prefs`
            """.trimIndent()
        )
        db.execSQL("DROP TABLE `user_prefs`")
        db.execSQL("ALTER TABLE `user_prefs_new` RENAME TO `user_prefs`")

        // Reset notification time to 0 for pomodoro or strict lock schedules
        db.execSQL(
            """
            UPDATE `schedules`
            SET `notification_time` = 0
            WHERE `is_pomodoro` = 1 OR `unlock_method` = 'StrictBlock'
            """.trimIndent()
        )
    }
}