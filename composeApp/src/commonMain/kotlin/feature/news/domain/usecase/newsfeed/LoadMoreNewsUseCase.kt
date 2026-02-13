package feature.news.domain.usecase.newsfeed

import core.domain.model.Result
import feature.news.domain.model.PaginationInfo
import feature.news.domain.repository.NewsFeedRepository

class LoadMoreNewsUseCase(
    private val repository: NewsFeedRepository
) {
    suspend operator fun invoke(): Result<PaginationInfo> {
        return repository.loadNextPage()
    }
}
