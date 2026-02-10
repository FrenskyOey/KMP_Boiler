package com.interview.prep.kmp_learn


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import core.theme.AppTheme
import feature.dashboard.ui.DashboardScreen
import feature.onboarding.domain.repository.AuthRepository
import feature.onboarding.ui.screen.LoginScreen
import feature.settings.ui.SettingsAction
import feature.settings.ui.button.ButtonScreen
import feature.settings.ui.color.ColorScreen
import feature.settings.ui.form.FormScreen
import feature.settings.ui.navbar.NavbarScreen
import feature.settings.ui.text.TextScreen
import org.koin.compose.koinInject
import core.domain.repository.SessionRepository
import core.domain.repository.SessionState
import kotlinx.coroutines.launch

@Composable
fun App() {
    val authRepository = koinInject<AuthRepository>()
    val sessionRepository = koinInject<SessionRepository>()
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    
    // Session Source of Truth
    val sessionState by sessionRepository.sessionState.collectAsState()
    
    AppTheme {
        // Handle Session State Changes
        LaunchedEffect(sessionState) {
            when (sessionState) {
                is SessionState.Valid -> {
                    navController.navigate("dashboard") {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is SessionState.Invalid -> {
                    // Only logout if we were previously logged in or checking (optimization)
                    // But effectively, Invalid means "Show Login"
                    authRepository.logout()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }

        NavHost(
            navController = navController,
            startDestination = "login" // Default start, will be overridden by LaunchedEffect immediately if Valid
        ) {
            composable("login") {
                LoginScreen()
            }

            composable("dashboard") {
                DashboardScreen(
                    onSettingsAction = { action ->
                        when (action) {
                            SettingsAction.OpenColor -> navController.navigate("settings_color")
                            SettingsAction.OpenText -> navController.navigate("settings_text")
                            SettingsAction.OpenButtons -> navController.navigate("settings_button")
                            SettingsAction.OpenForm -> navController.navigate("settings_form")
                            SettingsAction.OpenNavBar -> navController.navigate("settings_navbar")
                            SettingsAction.Logout -> {
                                coroutineScope.launch {
                                    sessionRepository.invalidateSession()
                                }
                            }
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
