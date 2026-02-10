package feature.news.domain.usecase.newsdetail

import core.domain.model.Result
import feature.news.domain.model.NewsDetail
import feature.news.domain.repository.NewsDetailRepository
import kotlinx.coroutines.flow.Flow

class GetNewsDetailUseCase(private val repository: NewsDetailRepository) {
    operator fun invoke(id: Int): Flow<Result<NewsDetail>> {
        return repository.getNewsDetail(id)
    }
}
