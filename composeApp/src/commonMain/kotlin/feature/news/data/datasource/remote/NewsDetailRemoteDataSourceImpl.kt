package feature.news.data.datasource.remote

import core.data.remote.util.ApiErrorHandler
import feature.news.data.api.NewsDetailApiService
import feature.news.data.datasource.NewsDetailDataSource
import feature.news.data.model.response.ArticleDetailResponse

class NewsDetailRemoteDataSourceImpl(
    private val apiService: NewsDetailApiService
) : NewsDetailDataSource.Remote {

    override suspend fun getNewsDetail(id: Int): ArticleDetailResponse {
        return try {
            apiService.getNewsDetail(id)
        } catch (e: Exception) {
            throw ApiErrorHandler.handleError(e)
        }
    }
}
