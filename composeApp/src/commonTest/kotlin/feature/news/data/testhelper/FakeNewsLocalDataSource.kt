package feature.news.data.testhelper

import feature.news.data.datasource.NewsDataSource
import feature.news.data.model.entity.ArticleEntity
import feature.news.data.model.entity.NewsRemoteKeysEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNewsLocalDataSource : NewsDataSource.Local {

    val articlesState = MutableStateFlow<List<ArticleEntity>>(emptyList())
    val remoteKeys = mutableListOf<NewsRemoteKeysEntity>()
    
    var clearArticlesCalled = false
    var clearRemoteKeysCalled = false

    override fun getArticles(limit: Int): Flow<List<ArticleEntity>> {
        return articlesState.map { it.take(limit) }
    }

    override suspend fun upsertArticles(articles: List<ArticleEntity>) {
        val current = articlesState.value.toMutableList()
        current.addAll(articles)
        articlesState.value = current
    }

    override suspend fun clearArticles() {
        clearArticlesCalled = true
        articlesState.value = emptyList()
    }

    override suspend fun getCount(): Int {
        return articlesState.value.size
    }

    override suspend fun getRemoteKeys(articleId: Long): NewsRemoteKeysEntity? {
        return remoteKeys.find { it.articleId == articleId }
    }

    override suspend fun getLastRemoteKey(): NewsRemoteKeysEntity? {
        // Imitate DB behavior: usually based on orderIndex or insertion order
        // For test simplicity, we assume the last added or last in list is sufficient if ordered
        return remoteKeys.sortedBy { it.orderIndex }.lastOrNull()
    }

    override suspend fun upsertRemoteKeys(keys: List<NewsRemoteKeysEntity>) {
        remoteKeys.addAll(keys)
    }

    override suspend fun clearRemoteKeys() {
        clearRemoteKeysCalled = true
        remoteKeys.clear()
    }
}
