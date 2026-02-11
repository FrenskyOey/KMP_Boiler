package feature.news.data.api

import feature.news.data.model.response.ArticleDetailResponse

interface NewsDetailApiService {
    suspend fun getNewsDetail(id: Long): ArticleDetailResponse
}
