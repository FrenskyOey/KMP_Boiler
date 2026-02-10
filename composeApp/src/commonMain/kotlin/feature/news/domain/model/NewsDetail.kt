package feature.news.domain.model

data class NewsDetail(
    val id: Int,
    val title: String,
    val category: String,
    val image: String,
    val author: Author,
    val publishedAt: String,
    val readTime: Int,
    val content: List<NewsContent>,
    val shareUrl: String
) {
    data class Author(
        val name: String,
        val avatar: String,
        val publication: String
    )
}

sealed interface NewsContent {
    data class Paragraph(val text: String) : NewsContent
    data class Quote(
        val text: String,
        val highlighted: Boolean = false,
        val emphasis: Boolean = false
    ) : NewsContent
}
