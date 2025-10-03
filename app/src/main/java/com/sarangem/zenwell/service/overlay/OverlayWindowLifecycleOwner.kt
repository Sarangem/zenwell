package com.sarangem.zenwell.service.overlay

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

@Suppress("unused")
class OverlayWindowLifecycleOwner : SavedStateRegistryOwner {

    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    private var mSavedStateRegistryController: SavedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle = mLifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry = mSavedStateRegistryController.savedStateRegistry

    // lifecycle functions
    fun setCurrentState(state: Lifecycle.State) { mLifecycleRegistry.currentState = state }
    fun handleLifecycleEvent(event: Lifecycle.Event) = mLifecycleRegistry.handleLifecycleEvent(event)
    fun performRestore(savedState: Bundle?) = mSavedStateRegistryController.performRestore(savedState)
    fun performSave(outBundle: Bundle) = mSavedStateRegistryController.performSave(outBundle)
}