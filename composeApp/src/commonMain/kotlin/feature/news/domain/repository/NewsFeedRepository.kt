package feature.news.domain.repository

import core.domain.model.Result
import feature.news.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsFeedRepository {
    fun getArticles(): Flow<List<Article>>
    suspend fun refresh(): Result<Unit>
    suspend fun loadNextPage(): Result<Unit>
}
