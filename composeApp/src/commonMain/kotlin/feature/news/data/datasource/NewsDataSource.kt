package feature.news.data.datasource

import feature.news.data.model.entity.ArticleEntity
import feature.news.data.model.response.ArticleResponse
import kotlinx.coroutines.flow.Flow

interface NewsDataSource {
    interface Remote{
        suspend fun fetchArticles(page: Int): List<ArticleResponse>
    }

    interface Local{
        fun getAllArticles(): Flow<List<ArticleEntity>>
        fun getArticleCount(): Flow<Int>
        suspend fun upsertArticles(articles: List<ArticleEntity>)
        suspend fun clearArticles()
    }
}