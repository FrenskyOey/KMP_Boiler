package feature.news.data.datasource.local

import feature.news.data.dao.NewsDao
import feature.news.data.dao.NewsRemoteKeysDao
import feature.news.data.datasource.NewsDataSource
import feature.news.data.model.entity.ArticleEntity
import feature.news.data.model.entity.NewsRemoteKeysEntity
import kotlinx.coroutines.flow.Flow

class NewsLocalDataSourceImpl(
    private val newsDao: NewsDao,
    private val newsRemoteKeysDao: NewsRemoteKeysDao
) : NewsDataSource.Local {

    override fun getArticles(limit: Int): Flow<List<ArticleEntity>> {
        return newsDao.getArticles(limit)
    }

    override suspend fun upsertArticles(articles: List<ArticleEntity>) {
        newsDao.insertAll(articles)
    }

    override suspend fun clearArticles() {
        newsDao.clearAll()
    }

    override suspend fun getCount(): Int {
        return newsDao.getCount()
    }

    override suspend fun getRemoteKeys(articleId: Long): NewsRemoteKeysEntity? {
        return newsRemoteKeysDao.getRemoteKeys(articleId)
    }

    override suspend fun getLastRemoteKey(): NewsRemoteKeysEntity? {
        return newsRemoteKeysDao.getLastRemoteKey()
    }

    override suspend fun upsertRemoteKeys(keys: List<NewsRemoteKeysEntity>) {
        newsRemoteKeysDao.insertAll(keys)
    }

    override suspend fun clearRemoteKeys() {
        newsRemoteKeysDao.clearRemoteKeys()
    }
}
