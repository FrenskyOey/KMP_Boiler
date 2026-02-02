package feature.news.data.local

import feature.news.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

interface NewsLocalDataSource {
    fun getAllArticles(): Flow<List<ArticleEntity>>
    fun getArticleCount(): Flow<Int>
    suspend fun upsertArticles(articles: List<ArticleEntity>)
    suspend fun clearArticles()
}
