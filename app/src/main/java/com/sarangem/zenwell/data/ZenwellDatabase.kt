package com.sarangem.zenwell.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sarangem.zenwell.data.tables.*

@Database(entities = [Schedules::class, AppNames::class, BlockedApps::class], version = 6, exportSchema = false)
abstract class ZenwellDatabase : RoomDatabase() {

    abstract fun scheduleDao() : ScheduleDao

    companion object {
        @Volatile
        private var Instance : ZenwellDatabase? = null

        fun getDatabase(context : Context) : ZenwellDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ZenwellDatabase::class.java,
                    "zenwell_database"
                )
                .fallbackToDestructiveMigration(false)
                .build()
                .also { Instance = it}
            }
        }
    }
}