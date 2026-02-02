package feature.news.data.datasource.remote

import core.domain.model.AppException
import feature.news.data.datasource.NewsDataSource
import feature.news.data.model.response.ArticleResponse
import feature.news.data.api.NewsApiService

class NewsRemoteDataSourceImpl(
    private val apiService: NewsApiService
) : NewsDataSource.Remote {

    override suspend fun fetchArticles(page: Int): List<ArticleResponse> {
        try {
            val response = apiService.fetchArticles(page)
            if (response.isSuccess) {
                return response.data ?: emptyList()
            } else {
                throw AppException.ServerError(code = 0, errorMessage = response.errorMessage)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}