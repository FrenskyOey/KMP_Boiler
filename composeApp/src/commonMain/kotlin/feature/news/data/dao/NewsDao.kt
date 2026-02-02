package feature.news.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import feature.news.data.model.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM article_entity")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Upsert
    suspend fun upsertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM article_entity")
    suspend fun clearArticles()

    @Query("SELECT COUNT(*) FROM article_entity")
    fun getArticleCount(): Flow<Int>
}