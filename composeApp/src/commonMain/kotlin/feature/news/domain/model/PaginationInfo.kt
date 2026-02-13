package feature.news.domain.model

data class PaginationInfo(
    val hasEndReached: Boolean,
    val currentLimit: Int
)
