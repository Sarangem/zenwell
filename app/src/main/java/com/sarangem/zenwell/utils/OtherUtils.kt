package com.sarangem.zenwell.utils

import com.sarangem.zenwell.service.AppBlockerService

fun checkAccessibilityServicePermission(): Boolean {
    return (AppBlockerService.instance != null)
}

fun isExpandedWidth(width: Float): Boolean {
    return width >= 840
}