package feature.news.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import core.domain.model.Result
import feature.news.domain.usecase.newsfeed.GetNewsFeedUseCase
import feature.news.domain.usecase.newsfeed.LoadMoreNewsUseCase
import feature.news.domain.usecase.newsfeed.RefreshNewsFeedUseCase
import feature.news.ui.main.state.NewsEffect
import feature.news.ui.main.state.NewsIntent
import feature.news.ui.main.state.NewsState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsFeedViewModel(
    private val getNewsFeedUseCase: GetNewsFeedUseCase,
    private val refreshNewsFeedUseCase: RefreshNewsFeedUseCase,
    private val loadMoreNewsUseCase: LoadMoreNewsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<NewsEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        observeArticles()
    }

    private fun observeArticles() {
        viewModelScope.launch {
            getNewsFeedUseCase().collect { articles ->
                _uiState.update { 
                    it.copy(
                        articles = articles,
                        isLoading = false,
                        isPaginationLoading = false,
                        isRefreshing = false
                    ) 
                }
            }
        }
    }

    fun onIntent(intent: NewsIntent) {
        when (intent) {
            NewsIntent.LoadNextPage -> loadNextPage()
            NewsIntent.Refresh -> refresh()
            NewsIntent.Retry -> retry()
        }
    }

    private fun loadNextPage() {
        // Prevent concurrent pagination requests
        if (_uiState.value.isPaginationLoading || _uiState.value.isEndReached) return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isPaginationLoading = true) }
            
            when (val result = loadMoreNewsUseCase()) {
                is Result.Success -> {
                    // Flow will emit updated list automatically
                    // isPaginationLoading will be reset by observeArticles
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isPaginationLoading = false) }
                    _effect.send(NewsEffect.ShowError(result.exception.message ?: "Failed to load more"))
                }
                is Result.Loading -> {
                    // Already handled by isPaginationLoading = true above
                }
            }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null, isEndReached = false) }
            
            when (val result = refreshNewsFeedUseCase()) {
                is Result.Success -> {
                    // Flow will emit updated list automatically
                    // isRefreshing will be reset by observeArticles
                }
                is Result.Error -> {
                    _uiState.update { it.copy(isRefreshing = false) }
                    _effect.send(NewsEffect.ShowError(result.exception.message ?: "Failed to refresh"))
                }
                is Result.Loading -> {
                    // Already handled by isRefreshing = true above
                }
            }
        }
    }

    private fun retry() {
        if (_uiState.value.articles.isEmpty()) {
            // Retry initial load by refreshing
            refresh()
        } else {
            // Retry pagination
            loadNextPage()
        }
    }
}
