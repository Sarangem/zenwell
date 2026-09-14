/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.overlay

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.overlay.common.APP_BLOCKED
import com.sarangem.zenwell.ui.overlay.common.OpenableMessageCard
import com.sarangem.zenwell.ui.overlay.common.OverlayScaffold
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import com.sarangem.zenwell.ui.theme.sizing
import kotlin.comparisons.compareBy

@Composable
fun TasksScreen(
    modifier: Modifier = Modifier,
    message: String = APP_BLOCKED,
    tasksList: List<TaskItem> = listOf(),
    updateTasksList: (List<TaskItem>) -> Unit = {},
    requireManualUnlock: Boolean = false,
    onTimerEnd: () -> Unit = {},
    showExit: Boolean = true,
    onExit: () -> Unit = {}
) {
    var sortedTasks by remember(tasksList) {
        mutableStateOf(
            tasksList.sortedWith(compareBy({ it.status == TaskStatus.DONE }, { it.id }))
        )
    }
    var showOpen by remember { mutableStateOf(false) }
    val handleToggle: (Int) -> Unit = { id ->
        sortedTasks = sortedTasks
            .map { current ->
                if (current.id == id) current.copy(status = current.status.next())
                else current
            }
            .sortedWith(compareBy({ it.status == TaskStatus.DONE }, { it.id }))
        updateTasksList(sortedTasks)
        if (sortedTasks.isNotEmpty() && sortedTasks.all { it.status == TaskStatus.DONE }) {
            if (!requireManualUnlock) onTimerEnd()
            showOpen = true
        }
    }

    if (message.isEmpty() && !requireManualUnlock) {
        OverlayScaffold(
            modifier = modifier.fillMaxSize(),
            showExit = showExit,
            onExit = onExit,
            content = { TasksColumn(Modifier.fillMaxSize(), sortedTasks, handleToggle) }
        )
    } else if (message.isEmpty()) {
        OverlayScaffold(
            modifier = modifier.fillMaxSize(),
            showExit = showExit,
            onExit = onExit,
            content = {
                AnimatedContent(
                    targetState = showOpen,
                    transitionSpec = { fadeIn(tween()) togetherWith fadeOut(tween()) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(MaterialTheme.sizing.small)
                ) { isOpen ->
                    if (isOpen) {
                        OpenableMessageCard(
                            modifier = Modifier.fillMaxSize(),
                            showOpen = true,
                            onTimerEnd = onTimerEnd
                        )
                    } else { TasksColumn(Modifier.fillMaxSize(), sortedTasks, handleToggle) }
                }
            }
        )
    } else {
        OverlayScaffold(
            modifier = modifier.fillMaxSize(),
            showExit = showExit,
            onExit = onExit,
            message = message,
            showOpen = showOpen,
            onTimerEnd = onTimerEnd,
            mainPane = { TasksColumn(it, sortedTasks, handleToggle) },
            mainPaneColumnWeight = 0.7f,
            mainPaneRowWeight = 0.6f
        )
    }
}

@Composable
fun TasksColumn(
    modifier: Modifier = Modifier,
    sortedTasks: List<TaskItem>,
    onToggle: (Int) -> Unit = {}
){
    val strikeColor = MaterialTheme.colorScheme.onSurface

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.small)
    ) {
        items(
            items = sortedTasks,
            key = { it.id }
        ) { task ->
            val strikeProgress by animateFloatAsState(if (task.status == TaskStatus.DONE) 1f else 0f)
            Row(
                modifier = Modifier
                    .animateItem()
                    .padding(vertical = MaterialTheme.sizing.small)
                    .clickable(onClick = { onToggle(task.id) })
            ) {
                TaskStatusIndicator(Modifier.size(MaterialTheme.sizing.large), task.status)
                Spacer(modifier = Modifier.width(MaterialTheme.sizing.medium))
                Text(
                    text = task.text,
                    fontSize = 24.sp,
                    fontWeight = if (task.status == TaskStatus.IN_PROGRESS) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier
                        .weight(1f)
                        .drawWithContent {
                            drawContent()
                            if (strikeProgress > 0f) {
                                val strokeWidth = 2.dp.toPx()
                                val y = size.height / 2f
                                drawLine(
                                    color = strikeColor,
                                    start = Offset(0f, y),
                                    end = Offset(size.width * strikeProgress, y),
                                    strokeWidth = strokeWidth
                                )
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun TaskStatusIndicator(modifier: Modifier, status: TaskStatus) {
    val color = MaterialTheme.colorScheme.primary
    val shape = MaterialTheme.shapes.extraLargeIncreased
    AnimatedContent(status){
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .clip(shape)
                .border(1.dp, color, shape)
                .drawBehind {
                    when (it) {
                        TaskStatus.TODO -> Unit
                        TaskStatus.DONE -> { drawRect(color = color) }
                        TaskStatus.IN_PROGRESS -> {
                            val path = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width, size.height)
                                lineTo(0f, size.height)
                                close()
                            }
                            drawPath(path = path, color = color)
                        }
                    }
                }
        ) {
            if (status == TaskStatus.DONE) {
                Icon(
                    painter = painterResource(id = R.drawable.filled_check),
                    contentDescription = stringResource(R.string.task_completed),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.fillMaxSize(0.75f)
                )
            }
        }
    }
}

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

data class TaskItem(
    val id: Int = 0,
    val text: String = "",
    val status: TaskStatus = TaskStatus.TODO
)

@Preview
@Composable
fun TasksScreenPreview() {
    ZenwellTheme {
        TasksScreen(
            tasksList = listOf(
                TaskItem(id = 1, text = "Close your eyes", status = TaskStatus.DONE),
                TaskItem(id = 2, text = "Stretch your hands", status = TaskStatus.IN_PROGRESS),
                TaskItem(id = 3, text = "Rub your eyes", status = TaskStatus.TODO)
            )
        )
    }
}