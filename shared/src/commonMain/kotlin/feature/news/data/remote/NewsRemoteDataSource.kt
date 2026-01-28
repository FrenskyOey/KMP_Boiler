package feature.news.data.remote

import feature.news.data.model.ArticleResponse

interface NewsRemoteDataSource {
    suspend fun fetchArticles(page: Int): List<ArticleResponse>
}
