package feature.news.data.api

import core.domain.config.AppConfig
import feature.news.data.model.response.ArticleListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NewsApiServiceImp(
    private val httpClient: HttpClient,
    private val appConfig: AppConfig
) : NewsApiService {

    // Append 'article' to the base URL
    private val endpoint = "${appConfig.baseApiUrl}article"

    override suspend fun fetchArticles(page: Int): ArticleListResponse {
        return httpClient.get(endpoint) {
            parameter("page", page)
        }.body()
    }
}