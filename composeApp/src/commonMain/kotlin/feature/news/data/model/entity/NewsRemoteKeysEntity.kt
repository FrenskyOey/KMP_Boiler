package feature.news.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news_remote_keys")
data class NewsRemoteKeysEntity(
    @PrimaryKey(autoGenerate = false)
    val articleId: Long,
    val prevKey: Int?,
    val nextKey: Int?,
    val createdAt: Long = 0L,
    val orderIndex: Int, // Crucial for ordering from API
    val isEndReached: Boolean = false // Track if this is the last page
)
