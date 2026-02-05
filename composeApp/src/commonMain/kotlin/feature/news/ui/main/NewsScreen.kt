package feature.news.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import core.components.CoreBasicAppBar
import feature.news.ui.main.components.NewsEmptyWidget
import feature.news.ui.main.components.NewsErrorWidget
import feature.news.ui.main.components.NewsItemWidget
import feature.news.ui.main.state.NewsEffect
import feature.news.ui.main.state.NewsIntent
import org.koin.compose.viewmodel.koinViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    listState: LazyListState,
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

    // is refresh is happen when user pull to refresh where the article data should not be empty

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CoreBasicAppBar(title = "News")
        PullToRefreshBox(
            isRefreshing = state.isRefresh,
            onRefresh = { viewModel.onIntent(NewsIntent.Refresh) },
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.isLoading && state.error != null && state.articles.isEmpty()) {
                NewsErrorWidget(
                    message = state.error!!,
                    onRetry = { viewModel.onIntent(NewsIntent.Retry) }
                )
            } else if (!state.isLoading && state.articles.isEmpty()) {
                NewsEmptyWidget()
            } else {
                // Pagination detection
                val shouldLoadNext = remember {
                    derivedStateOf {
                        val layoutInfo = listState.layoutInfo
                        val totalItemsNumber = layoutInfo.totalItemsCount
                        val lastVisibleItemIndex =
                            (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1

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
                    itemsIndexed(state.articles, key = { index, _ -> index }) { _, article ->
                        NewsItemWidget(
                            article = article,
                            onClick = { /* Handle click if needed */ }
                        )
                    }

                    if (state.isLoading && !state.articles.isEmpty()) {
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
}
