package feature.news.domain.usecase

import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow

class GetArticleCountUseCase(
    private val repository: NewsFeedRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getArticleCount()
    }
}
