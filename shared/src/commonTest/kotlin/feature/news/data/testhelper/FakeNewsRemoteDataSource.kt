package feature.news.data.testhelper

import feature.news.data.model.ArticleResponse
import feature.news.data.remote.NewsRemoteDataSource

class FakeNewsRemoteDataSource : NewsRemoteDataSource {

    var shouldReturnError = false
    var mockArticles: List<ArticleResponse> = emptyList()

    override suspend fun fetchArticles(page: Int): List<ArticleResponse> {
        if (shouldReturnError) {
            throw Exception("Fake API Error")
        }
        return mockArticles
    }
}
