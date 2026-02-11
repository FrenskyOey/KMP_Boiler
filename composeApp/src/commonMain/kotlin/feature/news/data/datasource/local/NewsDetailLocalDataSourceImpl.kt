package feature.news.data.datasource.local

import feature.news.data.dao.NewsDetailDao
import feature.news.data.datasource.NewsDetailDataSource
import feature.news.data.model.entity.ArticleDetailEntity
import kotlinx.coroutines.flow.Flow

class NewsDetailLocalDataSourceImpl(
    private val dao: NewsDetailDao
) : NewsDetailDataSource.Local {

    override fun getNewsDetail(id: Long): Flow<ArticleDetailEntity?> {
        val xid = (id % 8).toInt()
        return dao.getNewsDetail(xid)
    }

    override suspend fun upsertNewsDetail(article: ArticleDetailEntity) {
        dao.upsertNewsDetail(article)
    }
}
