package feature.news.data.dao

import feature.news.data.model.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNewsDao : NewsDao {

    val articleList = MutableStateFlow<List<ArticleEntity>>(emptyList())
    var insertedArticles = mutableListOf<ArticleEntity>()
    var clearAllCalled = false

    override fun getArticles(limit: Int): Flow<List<ArticleEntity>> {
        return articleList.map { it.take(limit) } // Simulate LIMIT
    }

    override suspend fun insertAll(articles: List<ArticleEntity>) {
        insertedArticles.addAll(articles)
        // In a real join query, we would need keys to order them.
        // For this fake, we just append them to the flow source if we want to simulate updates.
        // But the repository joins with keys. 
        // For simplicity, let's just add them to the list.
        articleList.update { it + articles }
    }

    override suspend fun clearAll() {
        clearAllCalled = true
        articleList.value = emptyList()
        insertedArticles.clear()
    }

    override suspend fun getCount(): Int {
        return articleList.value.size
    }
}
