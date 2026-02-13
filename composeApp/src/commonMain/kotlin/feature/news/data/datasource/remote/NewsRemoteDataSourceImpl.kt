package feature.news.data.datasource.remote

import core.data.remote.model.BaseListResponse
import core.data.remote.util.ApiErrorHandler
import feature.news.data.api.NewsApiService
import feature.news.data.datasource.NewsDataSource
import feature.news.data.model.response.ArticleResponse

class NewsRemoteDataSourceImpl(
    private val apiService: NewsApiService
) : NewsDataSource.Remote {

    override suspend fun fetchArticles(keyId: Int?): BaseListResponse<ArticleResponse> {
        return try {
            apiService.fetchArticles(keyId)
        } catch (e: Exception) {
            throw ApiErrorHandler.handleError(e)
        }
    }
}
