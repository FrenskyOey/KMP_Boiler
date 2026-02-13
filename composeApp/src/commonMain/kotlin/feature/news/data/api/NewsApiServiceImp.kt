package feature.news.data.api

import core.data.remote.model.BaseListResponse
import core.domain.config.AppConfig
import feature.news.data.model.response.ArticleResponse
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

    override suspend fun fetchArticles(keyId: Int?): BaseListResponse<ArticleResponse> {
        return httpClient.get(endpoint) {
            if (keyId != null) {
                parameter("key_id", keyId)
            }
        }.body()
    }
}