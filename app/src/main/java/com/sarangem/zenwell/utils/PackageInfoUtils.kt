/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build

data class PackageInfo(
    val packageName: String = "",
    val appName: String = "",
    val icon: Drawable? = null
)

fun getInstalledApps(context: Context): List<PackageInfo> {
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN, null)
        .addCategory(Intent.CATEGORY_LAUNCHER)

    val resolvedInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.queryIntentActivities(mainIntent, PackageManager.ResolveInfoFlags.of(0))
    } else {
        pm.queryIntentActivities(mainIntent, 0)
    }

    val packageInfoList: MutableList<PackageInfo> = mutableListOf()
    resolvedInfos.forEach { info ->
        val resources = pm.getResourcesForApplication(info.activityInfo.applicationInfo)

        packageInfoList.add(
            PackageInfo(
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

    return packageInfoList
}

fun getAppNameFromPackageName(context: Context, packageName: String): String? {
    val pm = context.packageManager
    try {
        val applicationInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getApplicationInfo(packageName, PackageManager.ApplicationInfoFlags.of(0))
        } else {
            pm.getApplicationInfo(packageName, 0)
        }
        return pm.getApplicationLabel(applicationInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        ServiceLogger.e({ "$packageName not found." }, e)
        return null
    }
}