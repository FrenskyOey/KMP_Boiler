package feature.news.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailResponse(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("category") val category: String,
    @SerialName("image") val image: String,
    @SerialName("author") val author: AuthorResponse,
    @SerialName("publishedAt") val publishedAt: String,
    @SerialName("readTime") val readTime: Int,
    @SerialName("content") val content: List<ContentItemResponse>,
    @SerialName("tags") val tags: List<String>?,
    @SerialName("shareUrl") val shareUrl: String
)

@Serializable
data class AuthorResponse(
    @SerialName("name") val name: String,
    @SerialName("avatar") val avatar: String,
    @SerialName("publication") val publication: String
)

@Serializable
data class ContentItemResponse(
    @SerialName("type") val type: String,
    @SerialName("text") val text: String,
    @SerialName("highlighted") val highlighted: Boolean? = null,
    @SerialName("emphasis") val emphasis: Boolean? = null
)
