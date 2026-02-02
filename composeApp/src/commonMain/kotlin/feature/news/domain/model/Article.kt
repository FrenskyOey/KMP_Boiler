package feature.news.domain.model

data class Article(
    val id: Long,
    val title: String,
    val content: String,
    val imageUrl: String,
    val topic: String
)
