/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.stats

import android.app.AppOpsManager
import android.content.Context
import android.os.Process
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarangem.zenwell.database.repository.SchedulesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val schedulesRepository: SchedulesRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()
    fun updateUiState(uiState: StatsUiState) {
        _uiState.update { uiState }
    }

    fun getStats(context: Context) {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val isGranted = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        ) == AppOpsManager.MODE_ALLOWED
        _uiState.update { it.copy(isPermissionGranted = isGranted) }

        if(isGranted){
            viewModelScope.launch(Dispatchers.IO) {
                _uiState.update { state ->
                    val dailyUsage = getDailyAppUsage(context, _uiState)
                    val blockedApps = schedulesRepository.getAllApps()
                        .firstOrNull()
                        ?.map { it.title.substringBefore(":id/") }
                        ?.distinct() ?: listOf()
                    state.copy(
                        dailyUsage = dailyUsage,
                        weeklyAverageInMinutes = getWeeklyAppUsageAverageInMinutes(context),
                        blockedApps = blockedApps,
                        filteredDailyUsage = filterDailyUsage(dailyUsage, blockedApps, state.statsFilter),
                        isLoading = false
                    )
                }
            }
        }
    }
}