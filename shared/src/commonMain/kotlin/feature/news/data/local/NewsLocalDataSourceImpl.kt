package feature.news.data.local

import feature.news.data.local.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

class NewsLocalDataSourceImpl(
    private val newsDao: NewsDao
) : NewsLocalDataSource {
    override fun getAllArticles(): Flow<List<ArticleEntity>> {
        return newsDao.getAllArticles()
    }

    override fun getArticleCount(): Flow<Int> {
        return newsDao.getArticleCount()
    }

    override suspend fun upsertArticles(articles: List<ArticleEntity>) {
        newsDao.upsertArticles(articles)
    }

    override suspend fun clearArticles() {
        newsDao.clearArticles()
    }
}
