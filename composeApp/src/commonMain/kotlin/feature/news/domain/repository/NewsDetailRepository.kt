package feature.news.domain.repository

import core.domain.model.Result
import feature.news.domain.model.NewsDetail
import kotlinx.coroutines.flow.Flow

interface NewsDetailRepository {
    fun getNewsDetail(id: Int): Flow<NewsDetail?>
    suspend fun refreshNewsDetail(id: Int): Result<Unit>
}
