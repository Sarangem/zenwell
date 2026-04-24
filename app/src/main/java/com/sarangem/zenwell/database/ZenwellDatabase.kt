/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.sarangem.zenwell.YOUTUBE_SHORTS_NAME
import com.sarangem.zenwell.YOUTUBE_SHORTS_VIEW_ID
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.database.tables.BlockedApps
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.database.typeconverters.OperatorListConverter
import com.sarangem.zenwell.database.typeconverters.WeekdaysListConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Schedules::class, AppNames::class, BlockedApps::class],
    version = 15,
    exportSchema = false
)
@TypeConverters(OperatorListConverter::class, WeekdaysListConverter::class)
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
                    .addCallback(ZenwellDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                    .fallbackToDestructiveMigration(false)
                    .build()
                    .also { Instance = it }
            }
        }

        private class ZenwellDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Instance?.let { database ->
                    scope.launch {
                        database.scheduleDao().insertAppNames(
                            AppNames(1, YOUTUBE_SHORTS_VIEW_ID, YOUTUBE_SHORTS_NAME)
                        )
                    }
                }
            }
        }

    }
}