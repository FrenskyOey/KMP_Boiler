package feature.news.data.datasource.local

import feature.news.data.datasource.NewsDataSource
import feature.news.data.dao.NewsDao
import feature.news.data.model.entity.ArticleEntity
import kotlinx.coroutines.flow.Flow

class NewsLocalDataSourceImpl(
    private val newsDao: NewsDao
) : NewsDataSource.Local {
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