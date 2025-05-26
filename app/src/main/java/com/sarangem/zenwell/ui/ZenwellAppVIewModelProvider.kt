package com.sarangem.zenwell.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.sarangem.zenwell.ZenwellApplication
import com.sarangem.zenwell.ui.editscreen.EditScreenViewModel
import com.sarangem.zenwell.ui.homescreen.HomeScreenViewModel

object ZenwellAppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            HomeScreenViewModel(yonocurbApplication().container)
        }

        initializer {
            EditScreenViewModel(yonocurbApplication().container)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ZenwellApplication].
 */
fun CreationExtras.yonocurbApplication(): ZenwellApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as ZenwellApplication)