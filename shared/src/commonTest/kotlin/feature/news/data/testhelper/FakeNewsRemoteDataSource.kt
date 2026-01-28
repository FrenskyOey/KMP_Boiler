package feature.news.data.testhelper

import feature.news.data.model.ArticleDto
import feature.news.data.remote.NewsRemoteDataSource

class FakeNewsRemoteDataSource : NewsRemoteDataSource {
    
    var fetchResult: Result<List<ArticleDto>>? = null
    var fetchedPage: Int? = null

    override suspend fun fetchArticles(page: Int): List<ArticleDto> {
        fetchedPage = page
        return fetchResult?.getOrThrow() ?: emptyList()
    }

    fun success(data: List<ArticleDto>) {
        fetchResult = Result.success(data)
    }

    fun failure(exception: Throwable) {
        fetchResult = Result.failure(exception)
    }
}
