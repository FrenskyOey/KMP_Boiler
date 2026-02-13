package feature.news.ui.main.state

import feature.news.domain.model.Article

data class NewsState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = true,  // Initial loading
    val isPaginationLoading: Boolean = false,  // Loading more items
    val isRefreshing: Boolean = false,  // Pull-to-refresh
    val error: String? = null,
    val isEndReached: Boolean = false
)

sealed interface NewsIntent {
    data object LoadNextPage : NewsIntent
    data object Refresh : NewsIntent
    data object Retry : NewsIntent
    data object CheckExpired : NewsIntent
}

sealed interface NewsEffect {
    data class ShowToast(val message: String) : NewsEffect
    data class ShowError(val message: String) : NewsEffect
}
