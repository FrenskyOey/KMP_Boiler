package feature.news.data.repository

import core.domain.model.Result
import feature.news.domain.model.Article
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// Placeholder implementation for Phase 2 (Domain) - to be implemented in Phase 3
class NewsFeedRepositoryImpl : NewsFeedRepository {
    override fun getArticles(page: Int): Flow<Result<List<Article>>> {
        return flowOf(Result.Success(emptyList()))
    }

    override fun getArticleCount(): Flow<Int> {
        return flowOf(0)
    }
}
