package feature.news.data.api

import core.data.remote.model.BaseResponse
import feature.news.data.model.response.ArticleDetailResponse

interface NewsDetailApiService {
    suspend fun getNewsDetail(id: Int): BaseResponse<ArticleDetailResponse>
}
