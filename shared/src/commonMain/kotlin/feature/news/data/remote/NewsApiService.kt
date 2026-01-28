package feature.news.data.remote

import core.domain.config.AppConfig
import feature.news.data.model.ArticleListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NewsApiService(
    private val httpClient: HttpClient,
    private val appConfig: AppConfig
) {
    
    // Append 'article' to the base URL
    private val endpoint = "${appConfig.baseApiUrl}article"

    suspend fun fetchArticles(page: Int): ArticleListResponse {
        return httpClient.get(endpoint) {
            parameter("page", page)
        }.body()
    }
}
