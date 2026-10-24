/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.model

data class TaskItem(
    val id: Int = 0,
    val text: String = "",
    val status: TaskStatus = TaskStatus.TODO
)

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    DONE;
    fun next(): TaskStatus = when (this) {
        TODO -> IN_PROGRESS
        IN_PROGRESS -> DONE
        DONE -> TODO
    }
}