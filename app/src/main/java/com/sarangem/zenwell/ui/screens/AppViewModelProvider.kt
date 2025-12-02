package com.sarangem.zenwell.ui.screens

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.ui.screens.edit.EditViewModel
import com.sarangem.zenwell.ui.screens.focus.FocusViewModel
import com.sarangem.zenwell.ui.screens.home.HomeViewModel

object AppViewModelProvider {

    fun CreationExtras.zenwellApplication(): ZenwellApplication =
        (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ZenwellApplication)

    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(zenwellApplication().container)
        }
        initializer {
            EditViewModel(zenwellApplication().container)
        }
        initializer {
            FocusViewModel()
        }
    }
}