package feature.news.data.api

import core.data.remote.model.BaseListResponse
import feature.news.data.model.response.ArticleResponse

interface NewsApiService{
    suspend fun fetchArticles(keyId: Int?): BaseListResponse<ArticleResponse>
}