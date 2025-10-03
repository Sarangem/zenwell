package com.sarangem.zenwell.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.ui.editscreen.EditViewModel
import com.sarangem.zenwell.ui.focusscreen.FocusViewModel
import com.sarangem.zenwell.ui.homescreen.HomeViewModel


object AppViewModelProvider {

    fun CreationExtras.zenwellApplication(): ZenwellApplication =
        (this[AndroidViewModelFactory.APPLICATION_KEY] as ZenwellApplication)

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
