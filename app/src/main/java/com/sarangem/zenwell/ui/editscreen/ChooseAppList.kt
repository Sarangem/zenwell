package com.sarangem.zenwell.ui.editscreen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseAppList(
    modifier: Modifier = Modifier,
    checkedAppList: List<String>?,
    updateAppList: (List<String>) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var installedAppList by remember { mutableStateOf<List<AppInfo>>(mutableListOf()) }
    LaunchedEffect(Unit) {
        launch {
            installedAppList = getInstalledApps(context).sortedBy { it.appName }
        }
    }

    EditScreenCard(
        modifier = modifier.clickable(onClick = { expanded = true })
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
                installedAppList = installedAppList,
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
    installedAppList: List<AppInfo>,
    checkedAppList: List<String>?,
    addAppToList: (String) -> Unit = {},
    removeAppFromList: (String) -> Unit = {}
) {

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
    app: AppInfo,
    checkedValue: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceDim),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.padding(1.dp))
        Image(
            painter = rememberDrawablePainter(app.icon),
            contentDescription = null,
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .clip(RoundedCornerShape(dimensionResource(R.dimen.padding_small)))
                .size(dimensionResource(R.dimen.image_size))
        )
        Spacer(Modifier.padding(dimensionResource(R.dimen.padding_small)))
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


// -- helper functions -- //

fun getInstalledApps(context: Context): MutableList<AppInfo> {
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN, null)
        .addCategory(Intent.CATEGORY_LAUNCHER)

    val resolvedInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0))
    } else {
        pm.queryIntentActivities(mainIntent, 0)
    }

    var appInfoList: MutableList<AppInfo> = mutableListOf()
    var resources: Resources
    resolvedInfos.forEach { info ->
        resources = pm.getResourcesForApplication(info.activityInfo.applicationInfo)

        appInfoList.add(
            AppInfo(
                packageName = info.activityInfo.packageName,
                appName = if (info.activityInfo.labelRes != 0) {
                    resources.getString(info.activityInfo.labelRes)
                } else {
                    info.activityInfo.applicationInfo.loadLabel(pm).toString()
                },
                icon = info.activityInfo.loadIcon(pm)
            )
        )
    }

    return appInfoList
}

data class AppInfo(
    val packageName: String = "",
    val appName: String = "",
    val icon: Drawable? = null
)


@Preview(showBackground = true)
@Composable
fun ChooseAppListPreview() {
    ZenwellTheme {
        ChooseAppList(
            checkedAppList = listOf()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShowBottomSheetPreview() {
    val icon = ContextCompat.getDrawable(LocalContext.current, R.drawable.ic_launcher_background)
    ZenwellTheme {
        BottomSheetContents(
            installedAppList = listOf(
                AppInfo(appName = "Calendar", icon = icon),
                AppInfo(appName = "Messages", icon = icon),
                AppInfo(appName = "Youtube", icon = icon)
            ),
            checkedAppList = null
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShowBottomSheetLoadingIndicatorPreview() {
    ZenwellTheme {
        BottomSheetContents(
            installedAppList = listOf(),
            checkedAppList = listOf()
        )
    }
}