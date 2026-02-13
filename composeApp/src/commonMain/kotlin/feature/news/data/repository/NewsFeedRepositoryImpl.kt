package feature.news.data.repository

import core.data.remote.util.ApiErrorHandler
import core.domain.model.Result
import feature.news.data.datasource.NewsDataSource
import feature.news.data.model.mapper.toDomain
import feature.news.data.model.mapper.toEntity
import feature.news.domain.model.Article
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NewsFeedRepositoryImpl(
    private val remoteDataSource: NewsDataSource.Remote,
    private val localDataSource: NewsDataSource.Local
) : NewsFeedRepository {

    override fun getArticles(): Flow<List<Article>> = flow {
        // TODO: Implement valid logic in Data Layer
        emit(emptyList())
    }

    override suspend fun refresh(): Result<Unit> {
        // TODO: Implement valid logic in Data Layer
        return Result.Success(Unit)
    }

    override suspend fun loadNextPage(): Result<Unit> {
        // TODO: Implement valid logic in Data Layer
        return Result.Success(Unit)
    }
}
