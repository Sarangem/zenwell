/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sarangem.zenwell.YOUTUBE_SHORTS_NAME
import com.sarangem.zenwell.YOUTUBE_SHORTS_VIEW_ID
import com.sarangem.zenwell.database.migrations.MIGRATION_19_20
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.database.tables.BlockedApps
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.database.tables.UserPreferences
import com.sarangem.zenwell.database.typeconverters.OperatorListConverter
import com.sarangem.zenwell.database.typeconverters.WeekdaysListConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Schedules::class,
        AppNames::class,
        BlockedApps::class,
        UserPreferences::class
    ],
    version = 20,
    autoMigrations = [
        AutoMigration(from = 18, to = 19)
    ],
    exportSchema = true
)
@TypeConverters(
    OperatorListConverter::class,
    WeekdaysListConverter::class
)
abstract class ZenwellDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var Instance: ZenwellDatabase? = null

        fun getDatabase(context: Context) : ZenwellDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ZenwellDatabase::class.java,
                    "zenwell_database"
                )
                    .addCallback(ZenwellDatabaseCallback())
                    .addMigrations(MIGRATION_19_20)
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }

        private class ZenwellDatabaseCallback() : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val scope = CoroutineScope(Dispatchers.IO)
                Instance?.let { database ->
                    scope.launch {
                        database.scheduleDao().upsertAppNames(
                            AppNames(1, YOUTUBE_SHORTS_VIEW_ID, YOUTUBE_SHORTS_NAME)
                        )
                    }
                }
            }
        }

    }
}