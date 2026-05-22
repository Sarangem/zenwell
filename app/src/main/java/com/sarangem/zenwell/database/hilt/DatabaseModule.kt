/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.database.hilt

import android.content.Context
import com.sarangem.zenwell.database.ScheduleDao
import com.sarangem.zenwell.database.ZenwellDatabase
import com.sarangem.zenwell.database.repository.OfflineSchedulesRepository
import com.sarangem.zenwell.database.repository.SchedulesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ZenwellDatabase {
        return ZenwellDatabase.getDatabase(context)
    }

    @Provides
    fun provideScheduleDao(db: ZenwellDatabase): ScheduleDao = db.scheduleDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSchedulesRepository(
        impl: OfflineSchedulesRepository
    ): SchedulesRepository
}