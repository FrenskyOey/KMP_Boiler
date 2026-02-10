package feature.news.data.repository

import core.domain.model.AppException
import core.domain.model.Result
import feature.news.data.datasource.NewsDetailDataSource
import feature.news.data.model.mapper.toDomain
import feature.news.data.model.mapper.toEntity
import feature.news.domain.model.NewsDetail
import feature.news.domain.repository.NewsDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NewsDetailRepositoryImpl(
    private val remoteDataSource: NewsDetailDataSource.Remote,
    private val localDataSource: NewsDetailDataSource.Local
) : NewsDetailRepository {

    override fun getNewsDetail(id: Int): Flow<NewsDetail?> {
        return localDataSource.getNewsDetail(id)
            .map { it?.toDomain() }
    }

    override suspend fun refreshNewsDetail(id: Int): Result<Unit> {
        return try {
            val response = remoteDataSource.getNewsDetail(id)
            localDataSource.upsertNewsDetail(response.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
            Result.Error(error)
        }
    }
}
