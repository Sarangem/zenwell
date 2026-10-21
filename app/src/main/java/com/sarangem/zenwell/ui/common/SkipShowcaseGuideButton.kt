/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.common.sequenceshowcase.LocalSequenceShowcaseState
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun SkipShowcaseGuideButton(onClick: () -> Unit) {
    val showcaseState = LocalSequenceShowcaseState.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopEnd
    ) {
        Button(
            modifier = Modifier
                .statusBarsPadding()
                .padding(MaterialTheme.sizing.small),
            onClick = {
                showcaseState.dismiss()
                onClick()
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondaryContainer),
            contentPadding = PaddingValues(horizontal = MaterialTheme.sizing.extraSmall)
        ) {
            Text(
                stringResource(R.string.skip_guide),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}