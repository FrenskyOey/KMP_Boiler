package feature.news.domain.model

data class NewsFeed(
    val id: String,
    val title: String,
    val content: String,
    val imageUrl: String?,
    val topic: String
)
