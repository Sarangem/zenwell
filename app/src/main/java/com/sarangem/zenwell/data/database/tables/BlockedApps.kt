package com.sarangem.zenwell.data.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "blocked_apps",
    foreignKeys = [
        ForeignKey (
            entity = Schedules::class,
            parentColumns = ["id"],
            childColumns = ["schedule_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AppNames::class,
            parentColumns = ["id"],
            childColumns = ["app_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["schedule_id"]),
        Index(value = ["app_id"])
    ]
)
data class BlockedApps(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // id of schedule referenced
    @ColumnInfo(name = "schedule_id")
    val scheduleId : Int = 0,

    // id of name of app referenced
    @ColumnInfo(name = "app_id")
    val appId : Int = 0,
)