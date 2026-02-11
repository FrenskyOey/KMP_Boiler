package feature.news.data.api

import core.domain.config.AppConfig
import feature.news.data.model.response.ArticleDetailResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NewsDetailApiServiceImpl(
    private val httpClient: HttpClient,
    private val appConfig: AppConfig
) : NewsDetailApiService {
    override suspend fun getNewsDetail(id: Long): ArticleDetailResponse {
        val xid = id % 8
        val url = "${appConfig.baseApiUrl}details"
        return httpClient.get(url) {
            parameter("xid", xid)
        }.body()
    }
}
