package feature.news.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import feature.news.data.model.entity.ArticleDetailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDetailDao {
    @Query("SELECT * FROM article_detail WHERE id = :id")
    fun getNewsDetail(id: Int): Flow<ArticleDetailEntity?>

    @Upsert
    suspend fun upsertNewsDetail(article: ArticleDetailEntity)
}
