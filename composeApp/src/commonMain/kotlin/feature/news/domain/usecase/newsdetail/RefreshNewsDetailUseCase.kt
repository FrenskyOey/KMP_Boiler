package feature.news.domain.usecase.newsdetail

import core.domain.model.Result
import feature.news.domain.repository.NewsDetailRepository

class RefreshNewsDetailUseCase(private val repository: NewsDetailRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.refreshNewsDetail(id)
    }
}
