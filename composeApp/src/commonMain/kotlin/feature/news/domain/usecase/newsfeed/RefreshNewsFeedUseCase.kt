package feature.news.domain.usecase.newsfeed

import core.domain.model.Result
import feature.news.domain.repository.NewsFeedRepository

class RefreshNewsFeedUseCase(
    private val repository: NewsFeedRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refresh()
    }
}
