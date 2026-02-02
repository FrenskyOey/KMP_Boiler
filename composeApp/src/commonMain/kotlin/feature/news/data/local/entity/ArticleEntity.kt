package feature.news.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_entity")
data class ArticleEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val content: String,
    @ColumnInfo(name = "image_url") val imageUrl: String,
    val topic: String
)
