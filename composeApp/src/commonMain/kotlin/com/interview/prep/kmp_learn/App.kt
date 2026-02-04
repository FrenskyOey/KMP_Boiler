package com.interview.prep.kmp_learn

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import core.theme.AppTheme
import feature.dashboard.ui.DashboardScreen
import feature.settings.ui.SettingsAction
import feature.settings.ui.button.ButtonScreen
import feature.settings.ui.color.ColorScreen
import feature.settings.ui.form.FormScreen
import feature.settings.ui.navbar.NavbarScreen
import feature.settings.ui.text.TextScreen

@Composable
fun App() {
    AppTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = "dashboard"
        ) {
            composable("dashboard") {
                DashboardScreen(
                    onSettingsAction = { action ->
                        when (action) {
                            SettingsAction.OpenColor -> navController.navigate("settings_color")
                            SettingsAction.OpenText -> navController.navigate("settings_text")
                            SettingsAction.OpenButtons -> navController.navigate("settings_button")
                            SettingsAction.OpenForm -> navController.navigate("settings_form")
                            SettingsAction.OpenNavBar -> navController.navigate("settings_navbar")
                        }
                    }
                )
            }

            composable("settings_color") {
                ColorScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("settings_text") {
                TextScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("settings_button") {
                ButtonScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("settings_form") {
                FormScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable("settings_navbar") {
                NavbarScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
