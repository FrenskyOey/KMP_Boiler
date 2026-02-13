package feature.news.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import feature.news.data.model.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {

    // Joined query to ensure order matches API response
    @Query("""
        SELECT * FROM articles 
        INNER JOIN news_remote_keys ON articles.id = news_remote_keys.articleId 
        ORDER BY news_remote_keys.orderIndex ASC 
        LIMIT :limit
    """)
    fun getArticles(limit: Int): Flow<List<ArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun getCount(): Int
}