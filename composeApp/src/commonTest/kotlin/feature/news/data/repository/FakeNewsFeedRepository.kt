package feature.news.data.repository

import core.domain.model.Result
import feature.news.domain.model.Article
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeNewsFeedRepository : NewsFeedRepository {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles = _articles.asStateFlow()

    var refreshResult: Result<Unit> = Result.Success(Unit)
    var loadMoreResult: Result<Unit> = Result.Success(Unit)

    var refreshCalled = false
    var loadMoreCalled = false

    override fun getArticles(): Flow<List<Article>> = articles

    override suspend fun refresh(): Result<Unit> {
        refreshCalled = true
        return refreshResult
    }

    override suspend fun loadNextPage(): Result<Unit> {
        loadMoreCalled = true
        return loadMoreResult
    }

    // Helper functions for tests
    fun emitArticles(newArticles: List<Article>) {
        _articles.update { newArticles }
    }
}
