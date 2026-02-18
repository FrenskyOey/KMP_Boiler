package feature.news.domain.usecase.newsfeed

import feature.news.domain.repository.NewsFeedRepository

class CheckCacheExpiredUseCase(
    private val repository: NewsFeedRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.isCacheExpired()
    }
}
