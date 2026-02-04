package feature.settings.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import feature.settings.ui.button.ButtonScreen
import feature.settings.ui.color.ColorScreen
import feature.settings.ui.form.FormScreen
import feature.settings.ui.main.SettingScreen
import feature.settings.ui.navbar.NavbarScreen
import feature.settings.ui.text.TextScreen

@Composable
fun SettingsNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "settings_main"
    ) {
        composable("settings_main") {
            SettingScreen(
                onColorClick = { navController.navigate("settings_color") },
                onTextClick = { navController.navigate("settings_text") },
                onButtonClick = { navController.navigate("settings_button") },
                onFormClick = { navController.navigate("settings_form") },
                onNavBarClick = {navController.navigate("settings_navbar")}
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
