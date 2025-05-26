package com.sarangem.zenwell.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarangem.zenwell.data.BlockType

@Entity(tableName = "schedules")
data class Schedules (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title : String = "",

    @ColumnInfo(name = "block_type")
    val blockType: BlockType = BlockType.FullBlock,

    // time of the day when blocking will start
    @ColumnInfo(name = "start_time")
    val startTimeInMinutes : Int = 0,

    // time of the day when blocking will end
    @ColumnInfo(name = "end_time")
    val endTimeInMinutes : Int = 0,

    // time to wait before opening the app
    // for specific block types only
    @ColumnInfo(name = "wait_time")
    val waitTimeInMinutes : Int = 0,

    // how long would the app be opened before again showing block message
    // for specific block types only
    @ColumnInfo(name = "open_time")
    val openTimeInMinutes : Int = 0,

    // parachute means skipping the block message. check if enabled.
    @ColumnInfo(name = "is_parachute")
    val isParachute : Boolean = false,

    // number of parachutes for each day
    @ColumnInfo(name = "parachute_count")
    val parachuteCount : Int = 0,

    // make the schedule of pomodoro type
    @ColumnInfo(name = "is_pomodoro")
    val isPomodoro : Boolean = false,

    // pomodoro working time
    // only if pomodoro enabled
    @ColumnInfo(name = "pomodoro_work_time")
    val pomodoroWorkTimeInMinutes : Int = 0,

    // pomodoro resting time
    // only if pomodoro enabled
    @ColumnInfo(name = "pomodoro_rest_time")
    val pomodoroRestTimeInMinutes : Int = 0,

    // if you forget to check phone during rest time, it would be auto-adjusted
    @ColumnInfo(name = "is_pomodoro_rest_adjustable")
    val isPomodoroRestAdjustable : Boolean = true,


    // Week days
    // Check if it will work on following week days
    val sunday: Boolean = true,
    val monday: Boolean = true,
    val tuesday: Boolean = true,
    val wednesday: Boolean = true,
    val thursday: Boolean = true,
    val friday: Boolean = true,
    val saturday: Boolean = true
)