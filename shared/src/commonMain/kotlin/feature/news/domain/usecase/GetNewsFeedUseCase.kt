package feature.news.domain.usecase

import core.domain.model.Result
import feature.news.domain.model.Article
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow

class GetNewsFeedUseCase(
    private val repository: NewsFeedRepository
) {
    operator fun invoke(page: Int): Flow<Result<List<Article>>> {
        return repository.getArticles(page)
    }
}
