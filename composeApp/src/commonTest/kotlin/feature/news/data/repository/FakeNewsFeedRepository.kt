package feature.news.data.repository

import core.domain.model.Result
import feature.news.domain.model.Article
import feature.news.domain.model.PaginationInfo
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeNewsFeedRepository : NewsFeedRepository {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles = _articles.asStateFlow()

    var refreshResult: Result<PaginationInfo> = Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 15))
    var loadMoreResult: Result<PaginationInfo> = Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 30))

    var refreshCalled = false
    var loadMoreCalled = false

    override fun getArticles(): Flow<List<Article>> = articles

    override suspend fun refresh(): Result<PaginationInfo> {
        refreshCalled = true
        return refreshResult
    }

    override suspend fun loadNextPage(): Result<PaginationInfo> {
        loadMoreCalled = true
        return loadMoreResult
    }

    override suspend fun isCacheExpired(): Boolean = false

    // Helper functions for tests
    fun emitArticles(newArticles: List<Article>) {
        _articles.update { newArticles }
    }
}
