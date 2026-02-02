package feature.news.data.repository

import core.data.remote.util.ApiErrorHandler
import core.domain.model.Result
import feature.news.data.local.NewsLocalDataSource
import feature.news.data.mapper.toDomain
import feature.news.data.mapper.toEntity
import feature.news.data.remote.NewsRemoteDataSource
import feature.news.domain.model.Article
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsFeedRepositoryImpl(
    private val remoteDataSource: NewsRemoteDataSource,
    private val localDataSource: NewsLocalDataSource
) : NewsFeedRepository {

    override fun getArticles(page: Int): Flow<Result<List<Article>>> = flow {
        emit(Result.Loading)
        try {
            val remoteArticles = remoteDataSource.fetchArticles(page)
            localDataSource.upsertArticles(remoteArticles.map { it.toEntity() })
            val domainArticles = remoteArticles.map { it.toDomain() }
            emit(Result.Success(domainArticles))
        } catch (e: Exception) {
            val error = ApiErrorHandler.handleError(e)
            emit(Result.Error(error))
        }
    }

    override fun getArticleCount(): Flow<Int> {
        return localDataSource.getArticleCount()
    }
}
