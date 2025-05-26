package com.sarangem.zenwell.ui.homescreen

import androidx.lifecycle.ViewModel
import com.sarangem.zenwell.data.SchedulesRepository
import com.sarangem.zenwell.data.tables.Schedules

class HomeScreenViewModel(private val schedulesRepository: SchedulesRepository) : ViewModel() {

    fun getAllSchedules() = schedulesRepository.getAllSchedules()

    suspend fun addNewSchedule(schedulesList: List<Schedules>): Int {

        var number = 1
        var title = createScheduleTitle(number)

        for (schedules in schedulesList) {
            if (schedules.title == title) {
                number++
                title = createScheduleTitle(number)
            }
        }
        return schedulesRepository.addNewSchedule(Schedules(title = title))
    }

    private fun createScheduleTitle(number: Int): String {
        return "Schedule $number"
    }

}