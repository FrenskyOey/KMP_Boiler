package feature.news.data.api

import core.data.remote.model.BaseListResponse
import feature.news.data.model.response.ArticleResponse

class FakeNewsApiService : NewsApiService {
    
    var fetchResult: BaseListResponse<ArticleResponse>? = null
    var requestedKeyId: Int? = null

    override suspend fun fetchArticles(keyId: Int?): BaseListResponse<ArticleResponse> {
        requestedKeyId = keyId
        return fetchResult ?: BaseListResponse(isSuccess = false, errorMessage = "No result set")
    }
}
