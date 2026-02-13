package feature.news.domain.repository

import core.domain.model.Result
import feature.news.domain.model.Article
import feature.news.domain.model.PaginationInfo
import kotlinx.coroutines.flow.Flow

interface NewsFeedRepository {
    fun getArticles(): Flow<List<Article>>
    suspend fun refresh(): Result<PaginationInfo>
    suspend fun loadNextPage(): Result<PaginationInfo>
    suspend fun isCacheExpired(): Boolean
}
