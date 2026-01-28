package feature.news.domain.repository

import core.domain.model.Result
import feature.news.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsFeedRepository {
    fun getArticles(page: Int): Flow<Result<List<Article>>>
    fun getArticleCount(): Flow<Int>
}
