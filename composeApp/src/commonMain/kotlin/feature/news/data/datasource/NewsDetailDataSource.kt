package feature.news.data.datasource

import feature.news.data.model.entity.ArticleDetailEntity
import feature.news.data.model.response.ArticleDetailResponse
import kotlinx.coroutines.flow.Flow

interface NewsDetailDataSource {
    interface Remote {
        suspend fun getNewsDetail(id: Int): ArticleDetailResponse
    }

    interface Local {
        fun getNewsDetail(id: Int): Flow<ArticleDetailEntity?>
        suspend fun upsertNewsDetail(article: ArticleDetailEntity)
    }
}
