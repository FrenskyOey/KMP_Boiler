package feature.news.data.api

import feature.news.data.model.response.ArticleListResponse

interface NewsApiService{
    suspend fun fetchArticles(page: Int): ArticleListResponse
}