package com.sarangem.zenwell.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.editscreen.EditScreen
import com.sarangem.zenwell.ui.homescreen.HomeScreen

enum class ZenwellScreen(val route: String, @StringRes val title: Int) {
    Home(route = "Home", title = R.string.home),
    EditSchedule(route = "Edit/{id}", title = R.string.edit_the_schedule),
    Settings(route = "Settings", title = R.string.settings);

    companion object {
        fun editScheduleWithId(id: Int) = "Edit/$id"
    }
}

@Composable
fun ZenwellApp() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = ZenwellScreen.Home.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(route = ZenwellScreen.Home.route) {
            HomeScreen(
                openEditScreen = { navController.navigate(ZenwellScreen.editScheduleWithId(it)) },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = ZenwellScreen.EditSchedule.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            EditScreen(
                scheduleId = backStackEntry.arguments?.getInt("id") ?: 0,
                goBack = {
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
