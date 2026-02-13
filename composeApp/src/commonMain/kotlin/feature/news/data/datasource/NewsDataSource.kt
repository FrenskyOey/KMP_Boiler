package feature.news.data.datasource

import core.data.remote.model.BaseListResponse
import feature.news.data.model.entity.ArticleEntity
import feature.news.data.model.entity.NewsRemoteKeysEntity
import feature.news.data.model.response.ArticleResponse
import kotlinx.coroutines.flow.Flow

interface NewsDataSource {
    interface Remote {
        suspend fun fetchArticles(keyId: Int?): BaseListResponse<ArticleResponse>
    }

    interface Local {
        fun getArticles(limit: Int): Flow<List<ArticleEntity>>
        suspend fun upsertArticles(articles: List<ArticleEntity>)
        suspend fun clearArticles()
        suspend fun getCount(): Int
        
        suspend fun getRemoteKeys(articleId: Long): NewsRemoteKeysEntity?
        suspend fun getLastRemoteKey(): NewsRemoteKeysEntity?
        suspend fun upsertRemoteKeys(keys: List<NewsRemoteKeysEntity>)
        suspend fun clearRemoteKeys()
    }
}
