package feature.news.ui.detail.state

import feature.news.domain.model.NewsDetail

data class NewsDetailState(
    val articleId: Long = 0,
    val articleTitle: String = "",
    val isLoading: Boolean = false,
    val article: NewsDetail? = null,
    val error: String? = null
)

sealed interface NewsDetailEvent {
    data class InitData(val id: Long, val title: String) : NewsDetailEvent
    data object OnRetry : NewsDetailEvent
    data class OnShareClick(val url: String) : NewsDetailEvent
    data object OnBackClick : NewsDetailEvent
}

sealed interface NewsDetailEffect {
    data class ShowSnackbar(val message: String) : NewsDetailEffect
    data class SharedClipBoard(val urlLink: String) : NewsDetailEffect
    data object NavigateBack : NewsDetailEffect
}
