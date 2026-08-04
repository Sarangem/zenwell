/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.home.permission

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.screens.home.SkipGuideButton
import com.sarangem.zenwell.ui.sequenceshowcase.sequenceShowcaseTarget
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun LazyItemScope.AccessibilityPermissionCard(
    recheckPermission: () -> Unit = {},
    showCard: Boolean = false,
    setAsExistingUser: () -> Unit = {}
){
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { recheckPermission() }

    AnimatedVisibility(
        showCard,
        modifier = Modifier.padding(MaterialTheme.sizing.small)
    ) {
        PermissionRequestCard(
            modifier = Modifier
                .animateItem()
                .then(showcase0Modifier(setAsExistingUser)),
            name = R.string.accessibility_service_permission,
            onGrantClick = { permissionLauncher.launch(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        )
    }

}

val showcase0Modifier: @Composable (skipGuide: () -> Unit) -> Modifier = { skip ->
    Modifier.sequenceShowcaseTarget(
        index = 0,
        shape = MaterialTheme.shapes.medium,
        shapeMargin = 0.dp,
        backgroundAlpha = 0.9f,
        fixedContent = { SkipGuideButton(skip) }
    ) {
        Text(
            text = stringResource(R.string.showcase_0),
            style = MaterialTheme.typography.headlineMedium,
            color = darkColorScheme().onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}