package feature.news.data.remote

import feature.news.data.model.ArticleListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NewsApiService(private val httpClient: HttpClient) {
    
    private val endpoint = "https://cd841015-7e2e-4a18-8082-a7c23d45097e.mock.pstmn.io/v1/article"

    suspend fun fetchArticles(page: Int): ArticleListResponse {
        return httpClient.get(endpoint) {
            parameter("page", page)
        }.body()
    }
}
