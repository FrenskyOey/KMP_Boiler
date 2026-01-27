package com.interview.prep.kmp_learn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import core.screens.HomeScreen
import core.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // For Phase 1: Show verification screen directly
                    HomeScreen(
                        flavorName = BuildConfig.FLAVOR_NAME,
                        apiUrl = BuildConfig.BASE_API_URL
                    )
                    
                    // For Phase 2: Use navigation
                    // val navController = rememberNavController()
                    // NavGraph(navController = navController)
                }
            }
        }
    }
}