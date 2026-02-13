package feature.news.data.repository

import core.domain.model.AppException
import core.domain.model.Result
import feature.news.data.datasource.NewsDataSource
import feature.news.data.model.entity.NewsRemoteKeysEntity
import feature.news.data.model.mapper.toDomain
import feature.news.data.model.mapper.toEntity
import feature.news.domain.model.Article
import feature.news.domain.model.PaginationInfo
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class NewsFeedRepositoryImpl(
    private val localDataSource: NewsDataSource.Local,
    private val remoteDataSource: NewsDataSource.Remote
) : NewsFeedRepository {

    // Helper to allow Flow collection to trigger re-fetches when limit changes
    private val currentLimit = MutableStateFlow(15)


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getArticles(): Flow<List<Article>> {
        return currentLimit.flatMapLatest { limit ->
            localDataSource.getArticles(limit).map { entities ->
                entities.map { it.toDomain() }
            }
        }
    }

    // Expose method for ViewModel to check cache expiry
    override suspend fun isCacheExpired(): Boolean {
        val lastKey = localDataSource.getLastRemoteKey() ?: return true
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val timeout = 60 * 60 * 1000L // 1 hour
        return (currentTime - lastKey.createdAt > timeout)
    }

    override suspend fun refresh(): Result<PaginationInfo> {
        return try {
            // Reset limit
            currentLimit.value = 15
            
            // 1. Fetch from API (Key = null for first page)
            val response = remoteDataSource.fetchArticles(keyId = null)
            
            if (response.isSuccess && response.data != null) {
                // Smart Invalidation Check
                val firstId = response.data.firstOrNull()?.id?.toLong() ?: 0L
                val firstKey = localDataSource.getRemoteKeys(firstId)
                val isSameChain = firstKey?.nextKey == response.pagination?.nextKeyId

                if (!isSameChain) {
                    // Clear key table due since history is not same anymore
                    localDataSource.clearRemoteKeys()
                }

                // Insert Cache
                val articles = response.data.map { it.toEntity(createdAt = Clock.System.now().toEpochMilliseconds()) }
                val isEndReached = response.pagination?.hasNext == false
                val keys = response.data.mapIndexed { index, article ->
                    NewsRemoteKeysEntity(
                        articleId = article.id.toLong(),
                        prevKey = null, // First page has no prev
                        nextKey = response.pagination?.nextKeyId,
                        createdAt = Clock.System.now().toEpochMilliseconds(),
                        orderIndex = index, // 0..14
                        isEndReached = isEndReached
                    )
                }
                
                localDataSource.upsertArticles(articles)
                localDataSource.upsertRemoteKeys(keys)
                
                Result.Success(
                    PaginationInfo(
                        hasEndReached = isEndReached,
                        currentLimit = currentLimit.value
                    )
                )
            } else {
                Result.Error(AppException.UnknownError(errorMessage = response.errorMessage ?: "Unknown API Error"))
            }   
        } catch (e: Exception) {
            val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
            Result.Error(error)
        }
    }

    override suspend fun loadNextPage(): Result<PaginationInfo> {
        return try {
            val limit = currentLimit.value
            val dbCount = localDataSource.getCount()

            // If we have more data in DB than currently shown, just increase limit
            if (dbCount > limit) {
                currentLimit.value += 15
                
                // Check isEndReached from the article at the current limit position
                val keyAtLimit = localDataSource.getRemoteKeyByOrderIndex(currentLimit.value -1)
                
                return Result.Success(
                    PaginationInfo(
                        hasEndReached = keyAtLimit?.isEndReached ?: false,
                        currentLimit = currentLimit.value
                    )
                )
            }

            val lastRemoteKey = localDataSource.getLastRemoteKey()
            val nextKey = lastRemoteKey?.nextKey

            // Otherwise, fetch from API
            val response = remoteDataSource.fetchArticles(keyId = nextKey)
            
            if (response.isSuccess && response.data != null) {
                val startOrderIndex = (lastRemoteKey?.orderIndex ?: 0) + 1
                val isEndReached = response.pagination?.hasNext == false
                
                val articles = response.data.map { it.toEntity(createdAt = Clock.System.now().toEpochMilliseconds()) }
                val keys = response.data.mapIndexed { index, article ->
                    NewsRemoteKeysEntity(
                        articleId = article.id.toLong(),
                        prevKey = null, 
                        nextKey = response.pagination?.nextKeyId,
                        createdAt = Clock.System.now().toEpochMilliseconds(),
                        orderIndex = startOrderIndex + index,
                        isEndReached = isEndReached
                    )
                }
                localDataSource.upsertArticles(articles)
                localDataSource.upsertRemoteKeys(keys)
                
                 // Increase limit to show new data
                currentLimit.value += 15
                Result.Success(
                    PaginationInfo(
                        hasEndReached = isEndReached,
                        currentLimit = currentLimit.value
                    )
                )
            } else {
                 Result.Error(AppException.UnknownError(errorMessage = response.errorMessage ?: "Unknown API Error"))
            }

        } catch (e: Exception) {
            val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
            Result.Error(error)
        }
    }
}
