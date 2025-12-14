package com.sarangem.zenwell.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sarangem.zenwell.database.tables.AppNames
import com.sarangem.zenwell.database.tables.BlockedApps
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.database.typeconverters.OperatorListConverter
import com.sarangem.zenwell.database.typeconverters.WeekdaysListConverter

@Database(entities = [Schedules::class, AppNames::class, BlockedApps::class], version = 13, exportSchema = false)
@TypeConverters(OperatorListConverter::class, WeekdaysListConverter::class)
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