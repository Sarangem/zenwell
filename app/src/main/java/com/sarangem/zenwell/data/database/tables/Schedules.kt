package com.sarangem.zenwell.data.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarangem.zenwell.data.BlockType

@Entity(tableName = "schedules")
data class Schedules(
    // by default, all recommended values are set

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // would be auto-generated

    val title: String = "", // would be set be viewmodel

    val message: String = "",

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = true,

    @ColumnInfo(name = "block_type")
    val blockType: BlockType = BlockType.FullBlock,

    // time of the day when blocking will start
    @ColumnInfo(name = "start_time")
    val startTimeInMinutes: Int = 0,

    // time of the day when blocking will end
    @ColumnInfo(name = "end_time")
    val endTimeInMinutes: Int = 1439,

    // time to wait before opening the app
    @ColumnInfo(name = "wait_time")
    val waitTimeInSeconds: Int = 10,

    // should the app use a button to enter or enter automatically
    @ColumnInfo(name = "wait_enter_button")
    val waitEnterButton: Boolean = true,

    // how long would the app be opened before again showing block message
    @ColumnInfo(name = "open_time")
    val openTimeInMinutes: Int = 10,

    // how long should a breathing cycle (one inhalation + one exhalation) take
    @ColumnInfo(name = "breathing_cycle_duration")
    val breathingCycleDuration: Int = 3,

    // how many breathing cycles there should be
    @ColumnInfo(name = "breathing_cycle_number")
    val breathingCycleNumber: Int = 2,

    @ColumnInfo(name = "notification_time")
    val notificationTimeInMinutes: Int = 2,

    // parachute means skipping the block message. check if enabled.
    @ColumnInfo(name = "is_parachute")
    val isParachute: Boolean = false,

    // number of parachutes for each day
    @ColumnInfo(name = "parachute_count")
    val parachuteCount: Int = 0,

    // make the schedule of pomodoro type
    @ColumnInfo(name = "is_pomodoro")
    val isPomodoro: Boolean = false,

    // pomodoro working time
    // only if pomodoro enabled
    @ColumnInfo(name = "pomodoro_work_time")
    val pomodoroWorkTimeInMinutes: Int = 25,

    // pomodoro resting time
    // only if pomodoro enabled
    @ColumnInfo(name = "pomodoro_rest_time")
    val pomodoroRestTimeInMinutes: Int = 5,

    // Check if it will work on following week days
    val weekDays: Int = 11111110
)