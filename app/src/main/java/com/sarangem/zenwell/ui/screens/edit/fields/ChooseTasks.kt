/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit.fields

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.em
import com.sarangem.zenwell.R

@Composable
fun ChooseTasks(
    tasks: String,
    updateTasks: (String) -> Unit = {}
) {
    DetailsCard {
        Text(
            text = stringResource(R.string.tasks_list),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        OutlinedTextField(
            modifier = Modifier.weight(2f),
            value = tasks,
            shape = MaterialTheme.shapes.large,
            onValueChange = updateTasks,
            visualTransformation = LineNumberTransformation
        )
    }
}

object LineNumberTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val lines = text.text.split('\n')
        val maxPrefixLength = "${lines.size}. ".length
        val out = buildAnnotatedString {
            lines.forEachIndexed { i, line ->
                val prefix = "${i + 1}. ".padEnd(maxPrefixLength)
                withStyle(SpanStyle(color = Color.Gray)) { append(prefix) }
                append(line)
                if (i != lines.lastIndex) append("\n")
            }
            addStyle(
                ParagraphStyle(textIndent = TextIndent(restLine = (maxPrefixLength * 0.55f).em)),
                0, length
            )
        }
        return TransformedText(out, object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var lineIdx = 0
                for (i in 0 until offset) {
                    if (text.text[i] == '\n') lineIdx++
                }
                return offset + (lineIdx + 1) * maxPrefixLength
            }
            override fun transformedToOriginal(offset: Int): Int {
                var orig = 0
                var trans = 0
                for ((i, line) in lines.withIndex()) {
                    trans += maxPrefixLength
                    if (offset < trans) return orig
                    val len = line.length + if (i == lines.lastIndex) 0 else 1
                    if (offset <= trans + len) return orig + (offset - trans)

                    orig += len
                    trans += len
                }
                return orig
            }
        })
    }
}