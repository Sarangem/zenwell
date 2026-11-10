/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ChooseTasks(
    tasks: String,
    updateTasks: (String) -> Unit = {}
) {
    val state = rememberTextFieldState(tasks)
    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }.collectLatest { updateTasks(it) }
    }
    var getLayout by remember { mutableStateOf<(() -> TextLayoutResult?)?>(null) }
    val layoutDirection = LocalLayoutDirection.current
    val outlineColor = MaterialTheme.colorScheme.outline

    DetailsCard {
        Text(
            text = stringResource(R.string.tasks_list),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            modifier = Modifier
                .weight(2f)
                .drawBehind {
                    val layout = getLayout?.invoke() ?: return@drawBehind
                    val contentPadding = OutlinedTextFieldDefaults.contentPaddingWithLabel()
                    val left = contentPadding.calculateLeftPadding(layoutDirection).toPx()
                    val top = (size.height - layout.getLineBottom(layout.lineCount - 1)) / 2
                    layout.layoutInput.text.forEachIndexed { i, c ->
                        if (c == '\n') {
                            val y = top + layout.getLineBottom(layout.getLineForOffset(i))
                            drawLine(
                                color = outlineColor,
                                start = Offset(left, y),
                                end = Offset(size.width - left, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
                },
            state = state,
            shape = MaterialTheme.shapes.large,
            onTextLayout = { getLayout = it },
            outputTransformation = OutputTransformation {
                var offset = 0
                asCharSequence().lines().forEachIndexed { i, line ->
                    val prefix = "${i + 1}. "
                    insert(offset, prefix)
                    addStyle(
                        spanStyle = SpanStyle(outlineColor),
                        start = offset,
                        end = offset + prefix.length
                    )
                    offset += prefix.length + line.length + 1
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChooseTasksPreview() {
    ZenwellTheme {
        ChooseTasks(
            tasks = "Close your eyes\nTake a deep breath\nLook outside"
        )
    }
}