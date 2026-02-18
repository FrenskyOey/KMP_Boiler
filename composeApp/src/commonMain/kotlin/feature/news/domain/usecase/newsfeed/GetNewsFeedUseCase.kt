package feature.news.domain.usecase.newsfeed

import feature.news.domain.model.Article
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow

class GetNewsFeedUseCase(
    private val repository: NewsFeedRepository
) {
    operator fun invoke(): Flow<List<Article>> {
        return repository.getArticles()
    }
}
