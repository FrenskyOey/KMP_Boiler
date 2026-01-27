package feature.news.domain.repository

import core.domain.model.Result
import feature.news.domain.model.NewsFeed

interface NewsFeedRepository {
    suspend fun getNewsFeed(): Result<List<NewsFeed>>
}
