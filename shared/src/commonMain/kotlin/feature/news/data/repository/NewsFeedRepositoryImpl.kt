package feature.news.data.repository

import core.domain.model.Result
import feature.news.domain.model.NewsFeed
import feature.news.domain.repository.NewsFeedRepository

// Placeholder implementation for Phase 1
class NewsFeedRepositoryImpl : NewsFeedRepository {
    override suspend fun getNewsFeed(): Result<List<NewsFeed>> {
        return Result.Success(emptyList())
    }
}
