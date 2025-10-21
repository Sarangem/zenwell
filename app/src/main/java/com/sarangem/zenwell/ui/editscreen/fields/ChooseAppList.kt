package com.sarangem.zenwell.ui.editscreen.fields

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.editscreen.details.DetailsCard
import com.sarangem.zenwell.utils.PackageInfo
import com.sarangem.zenwell.utils.getInstalledApps

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAppList(
    modifier: Modifier = Modifier,
    checkedAppList: List<String>?,
    updateAppList: (List<String>) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    DetailsCard(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .clickable(onClick = { expanded = true })
    ) {
        Text(
            text = stringResource(R.string.choose_apps),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = stringResource(R.string.show_apps_to_block)
            )
        }
    }


    if (expanded) {
        ModalBottomSheet(
            onDismissRequest = { expanded = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            BottomSheetContents(
                getInstalledApps = { getInstalledApps(it) },
                checkedAppList = checkedAppList,
                addAppToList = {
                    if (checkedAppList != null) updateAppList(checkedAppList.plus(it))
                },
                removeAppFromList = {
                    if (checkedAppList != null) updateAppList(checkedAppList.minus(it))
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomSheetContents(
    modifier: Modifier = Modifier,
    getInstalledApps: (Context) -> List<PackageInfo>,
    checkedAppList: List<String>?,
    addAppToList: (String) -> Unit = {},
    removeAppFromList: (String) -> Unit = {}
) {

    val context = LocalContext.current
    var installedAppList by remember { mutableStateOf<List<PackageInfo>>(listOf()) }
    LaunchedEffect(Unit) {
        installedAppList = getInstalledApps(context).sortedBy { it.appName }
    }

    if (installedAppList.isEmpty() || checkedAppList == null) {
        LoadingIndicator(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensionResource(R.dimen.image_size),
                    bottom = dimensionResource(R.dimen.image_size)
                )
        )
    } else {

        LazyColumn(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_large))),
        ) {
            items(installedAppList) { app ->
                AppCard(
                    app = app,
                    checkedValue = app.packageName in checkedAppList,
                    onCheckedChange = { value ->
                        if (value) {
                            addAppToList(app.packageName)
                        } else {
                            removeAppFromList(app.packageName)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.card_elevation))
                )
            }
        }

    }
}

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    app: PackageInfo,
    checkedValue: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceDim),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.padding(dimensionResource(R.dimen.card_elevation)))
        Image(
            painter = rememberDrawablePainter(app.icon),
            contentDescription = null,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                .size(dimensionResource(R.dimen.image_size))
        )
        Text(
            text = app.appName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))

        )
        Spacer(Modifier.weight(1f))
        Checkbox(
            checked = checkedValue,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        )
    }
}