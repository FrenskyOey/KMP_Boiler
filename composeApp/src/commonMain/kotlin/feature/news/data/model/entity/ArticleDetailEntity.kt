package feature.news.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import feature.news.data.model.response.AuthorResponse
import feature.news.data.model.response.ContentItemResponse

@Entity(tableName = "article_detail")
data class ArticleDetailEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val category: String,
    val image: String,
    val author: AuthorResponse,
    val publishedAt: String,
    val readTime: Int,
    val content: List<ContentItemResponse>,
    val tags: List<String>?,
    val shareUrl: String
)
