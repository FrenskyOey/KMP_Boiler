package feature.news.list

import feature.news.domain.model.Article

data class NewsState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val isEndReached: Boolean = false,
    val totalCachedCount: Int = 0,
    val isRefresh: Boolean = false
)

sealed interface NewsIntent {
    data object LoadNextPage : NewsIntent
    data object Refresh : NewsIntent
    data object Retry : NewsIntent
}

sealed interface NewsEffect {
    data class ShowToast(val message: String) : NewsEffect
    data class ShowError(val message: String) : NewsEffect
}
