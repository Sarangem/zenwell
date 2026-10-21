/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.domain.packageinfo

import android.graphics.drawable.Drawable

data class MyPackageInfo(
    val packageName: String = "",
    val appName: String = "",
    val icon: Drawable? = null,
    val viewId: String? = null,
    val viewKeyword: String? = null
)