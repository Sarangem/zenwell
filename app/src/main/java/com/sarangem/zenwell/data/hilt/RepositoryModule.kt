/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.data.hilt

import com.sarangem.zenwell.data.repository.OfflineSchedulesRepository
import com.sarangem.zenwell.data.repository.SchedulesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSchedulesRepository(
        impl: OfflineSchedulesRepository
    ): SchedulesRepository
}