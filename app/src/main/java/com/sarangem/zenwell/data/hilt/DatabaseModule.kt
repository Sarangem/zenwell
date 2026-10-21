/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.data.hilt

import android.content.Context
import com.sarangem.zenwell.data.ScheduleDao
import com.sarangem.zenwell.data.ZenwellDatabase
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