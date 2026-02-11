package feature.news.data.datasource.remote

import core.data.remote.util.ApiErrorHandler
import feature.news.data.api.NewsDetailApiService
import feature.news.data.datasource.NewsDetailDataSource
import feature.news.data.model.response.ArticleDetailResponse

import core.domain.model.AppException

class NewsDetailRemoteDataSourceImpl(
    private val apiService: NewsDetailApiService
) : NewsDetailDataSource.Remote {

    override suspend fun getNewsDetail(id: Long): ArticleDetailResponse {
        return try {
            val response = apiService.getNewsDetail(id)
            if (response.isSuccess) {
                response.data
            } else {
                throw AppException.UnknownError("API request failed with is_success=false")
            }
        } catch (e: Exception) {
            throw ApiErrorHandler.handleError(e)
        }
    }
}
