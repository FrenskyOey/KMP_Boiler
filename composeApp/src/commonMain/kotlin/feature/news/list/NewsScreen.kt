package feature.news.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsScreen(
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
                NewsTab.News -> NewsFeedContent(
                    onShowSnackbar = { message ->
                        snackbarHostState.showSnackbar(message)
                    }
                )
                NewsTab.Settings -> UnderMaintenanceScreen()
            }
        }
    }
}

enum class NewsTab { News, Settings }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewsFeedContent(
    onShowSnackbar: suspend (String) -> Unit,
    viewModel: NewsFeedViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NewsEffect.ShowError -> onShowSnackbar(effect.message)
                is NewsEffect.ShowToast -> onShowSnackbar(effect.message)
            }
        }
    }

    // Pull to refresh logic
    // We consider refreshing if loading is true AND we are at page 1 (and potentially verify empty list or not)
    // But since we want to show spinner ON TOP of content, we use PullToRefreshBox.
    // However, viewModel.isLoading is global.
    // If we are paginating (page > 1), we don't want the top spinner to show.
    // So isRefreshing = state.isLoading && state.page == 1
    
    val isRefreshing = state.isLoading && state.page == 1

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.onIntent(NewsIntent.Refresh) },
        modifier = Modifier.fillMaxSize()
    ) {
        if (!state.isLoading && state.error != null && state.articles.isEmpty()) {
            ErrorState(
                message = state.error!!,
                onRetry = { viewModel.onIntent(NewsIntent.Retry) }
            )
        } else if (!state.isLoading && state.articles.isEmpty()) {
            EmptyState()
        } else {
            val listState = rememberLazyListState()

            // Pagination detection
            val shouldLoadNext = remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val totalItemsNumber = layoutInfo.totalItemsCount
                    val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

                    lastVisibleItemIndex > (totalItemsNumber - 2)
                }
            }

            LaunchedEffect(shouldLoadNext.value) {
                if (shouldLoadNext.value && !state.isLoading && !state.isEndReached) {
                    viewModel.onIntent(NewsIntent.LoadNextPage)
                }
            }
            
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.articles, key = { it.id }) { article ->
                    ArticleItem(
                        article = article,
                        onClick = { /* Handle click if needed */ }
                    )
                }
                
                if (state.isLoading && state.page > 1) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
