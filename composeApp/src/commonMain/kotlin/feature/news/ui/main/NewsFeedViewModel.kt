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
        viewModelScope.launch {
            getNewsFeedUseCase().collect { articles ->
                _uiState.update { it.copy(articles = articles, isLoading = false) }
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
        if (_uiState.value.isLoading || _uiState.value.isEndReached) return
        viewModelScope.launch {
            loadMoreNewsUseCase()
        }
    }

    private fun refresh() {
        _uiState.update { it.copy(page = 1, isEndReached = false, error = null, isRefresh = true)}
        viewModelScope.launch {
            refreshNewsFeedUseCase()
        }
    }

    private fun retry() {
        if (_uiState.value.articles.isEmpty()) {
            // loadPage(1)
        } else {
            // loadPage(_uiState.value.page + 1)
        }
    }

    /*
    private fun loadPage(page: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getNewsFeedUseCase(page).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        // Already handled by manual update, or can handle here
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Result.Success -> {
                        val newArticles = result.data
                        if (newArticles.isEmpty()) {
                            _uiState.update { 
                                it.copy(isLoading = false, isEndReached = true, isRefresh = false)
                            }
                        } else {
                            val isRefreshed = _uiState.value.isRefresh

                            _uiState.update { 
                                it.copy(
                                    isLoading = false,
                                    articles = if (page == 1) newArticles else it.articles + newArticles,
                                    page = page,
                                    isRefresh = false
                                ) 
                            }
                            if (isRefreshed) {
                                _effect.send(NewsEffect.ShowToast("Refreshed"))
                            }
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.exception.message ?: "Unknown Error", isRefresh = false) }
                        _effect.send(NewsEffect.ShowError(result.exception.message ?: "Error loading news"))
                    }
                }
            }
        }
    }
    */
    
    // private fun observeArticleCount() { ... } removed
}
