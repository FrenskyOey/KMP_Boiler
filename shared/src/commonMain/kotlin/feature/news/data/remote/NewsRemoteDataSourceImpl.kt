package feature.news.data.remote

import core.domain.model.AppException
import feature.news.data.model.ArticleResponse

class NewsRemoteDataSourceImpl(
    private val apiService: NewsApiService
) : NewsRemoteDataSource {
    
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
