package feature.news.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import feature.news.domain.model.Article

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val title: String,
    val content: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    val topic: String,
    val createdAt: Long = 0L // For cache expiry
)