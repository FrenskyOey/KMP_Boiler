package feature.news.data.testhelper

import feature.news.data.local.NewsLocalDataSource
import feature.news.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeNewsLocalDataSource : NewsLocalDataSource {
    
    val savedArticles = mutableListOf<ArticleEntity>()
    var count = 0

    override fun getAllArticles(): Flow<List<ArticleEntity>> {
        return flowOf(savedArticles)
    }

    override fun getArticleCount(): Flow<Int> {
        return flowOf(count)
    }

    override suspend fun upsertArticles(articles: List<ArticleEntity>) {
        savedArticles.addAll(articles)
        count += articles.size
    }

    override suspend fun clearArticles() {
        savedArticles.clear()
        count = 0
    }
}
