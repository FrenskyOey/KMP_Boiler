package feature.news.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import core.domain.model.Result
import feature.news.domain.usecase.newsdetail.GetNewsDetailUseCase
import feature.news.domain.usecase.newsdetail.RefreshNewsDetailUseCase
import feature.news.ui.detail.state.NewsDetailEffect
import feature.news.ui.detail.state.NewsDetailEvent
import feature.news.ui.detail.state.NewsDetailState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewsDetailViewModel(
    private val getNewsDetailUseCase: GetNewsDetailUseCase,
    private val refreshNewsDetailUseCase: RefreshNewsDetailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsDetailState())
    val uiState = _uiState.asStateFlow()

    private val _effect = Channel<NewsDetailEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: NewsDetailEvent) {
        when (event) {
            is NewsDetailEvent.InitData -> initData(event.id, event.title)
            NewsDetailEvent.OnRetry -> retry()
            is NewsDetailEvent.OnShareClick -> share(event.url)
            NewsDetailEvent.OnBackClick -> navigateBack()
        }
    }

    private fun initData(id: Long, title: String) {
        _uiState.update { it.copy(articleId = id, articleTitle = title, isLoading = true, error = null) }
        observeDetail(id)
        refreshDetail(id)
    }

    private fun observeDetail(id: Long) {
        viewModelScope.launch {
            getNewsDetailUseCase(id).collect { article ->
                if (article != null) {
                    _uiState.update { it.copy(article = article, error = null) }
                }
            }
        }
    }

    private fun refreshDetail(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = refreshNewsDetailUseCase(id)
            when (result) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
                is Result.Error -> {
                    val hasData = _uiState.value.article != null
                    if (!hasData) {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                error = result.exception.message ?: "Unknown Error"
                            ) 
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                        _effect.send(NewsDetailEffect.ShowSnackbar(result.exception.message ?: "Error loading article"))
                    }
                }
                is Result.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    private fun retry() {
        val currentId = _uiState.value.articleId
        if (currentId != 0L) {
            refreshDetail(currentId)
        }
    }

    private fun share(url: String) {
        viewModelScope.launch {
            _effect.send(NewsDetailEffect.SharedClipBoard(url))
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.send(NewsDetailEffect.NavigateBack)
        }
    }
}
