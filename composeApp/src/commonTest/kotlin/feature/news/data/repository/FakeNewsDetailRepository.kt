package feature.news.data.repository

import core.domain.model.Result
import feature.news.domain.model.NewsDetail
import feature.news.domain.repository.NewsDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class FakeNewsDetailRepository : NewsDetailRepository {

    private val _newsDetail = MutableStateFlow<Map<Long, NewsDetail>>(emptyMap())
    
    var refreshResult: Result<Unit> = Result.Success(Unit)
    var refreshCalled = false

    override fun getNewsDetail(id: Long): Flow<NewsDetail?> {
        return _newsDetail.map { it[id] }
    }

    override suspend fun refreshNewsDetail(id: Long): Result<Unit> {
        refreshCalled = true
        return refreshResult
    }

    // Helper to set data
    fun emitNewsDetail(id: Long, detail: NewsDetail) {
        _newsDetail.value = _newsDetail.value + (id to detail)
    }
}
