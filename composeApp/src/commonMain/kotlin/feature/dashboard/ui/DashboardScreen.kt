package feature.dashboard.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import feature.news.ui.main.NewsScreen
import feature.settings.ui.main.SettingScreen

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(NewsTab.News) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == NewsTab.News,
                    onClick = { selectedTab = NewsTab.News },
                    icon = { Icon(Icons.Default.Home, contentDescription = "News") },
                    label = { Text("News") }
                )
                NavigationBarItem(
                    selected = selectedTab == NewsTab.Settings,
                    onClick = { selectedTab = NewsTab.Settings },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                NewsTab.News -> NewsScreen(
                    onShowSnackbar = { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                )
                NewsTab.Settings -> SettingScreen()
            }
        }
    }
}

enum class NewsTab { News, Settings }