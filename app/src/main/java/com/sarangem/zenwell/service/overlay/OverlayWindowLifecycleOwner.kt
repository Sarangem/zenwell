/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.service.overlay

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class OverlayWindowLifecycleOwner : SavedStateRegistryOwner {

    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle = mLifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry = mSavedStateRegistryController.savedStateRegistry

    fun handleLifecycleEvent(event: Lifecycle.Event) = mLifecycleRegistry.handleLifecycleEvent(event)
    fun performRestore(savedState: Bundle?) = mSavedStateRegistryController.performRestore(savedState)
}