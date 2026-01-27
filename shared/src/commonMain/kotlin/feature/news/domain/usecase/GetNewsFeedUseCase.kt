package feature.news.domain.usecase

import core.domain.model.Result
import feature.news.domain.model.NewsFeed
import feature.news.domain.repository.NewsFeedRepository

class GetNewsFeedUseCase(
    private val repository: NewsFeedRepository
) {
    suspend operator fun invoke(): Result<List<NewsFeed>> {
        return repository.getNewsFeed()
    }
}
