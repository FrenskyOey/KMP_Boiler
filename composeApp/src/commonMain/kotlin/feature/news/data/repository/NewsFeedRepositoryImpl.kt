package feature.news.data.repository

import core.data.local.util.TransactionProvider
import core.domain.model.AppException
import core.domain.model.Result
import feature.news.data.datasource.NewsDataSource
import feature.news.data.model.entity.NewsRemoteKeysEntity
import feature.news.data.model.mapper.toDomain
import feature.news.data.model.mapper.toEntity
import feature.news.domain.model.Article
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class NewsFeedRepositoryImpl(
    private val remoteDataSource: NewsDataSource.Remote,
    private val localDataSource: NewsDataSource.Local,
    private val transactionProvider: TransactionProvider
) : NewsFeedRepository {

    // Helper to allow Flow collection to trigger re-fetches when limit changes
    private val currentLimit = MutableStateFlow(15)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getArticles(): Flow<List<Article>> = flow {
        // Initial cache expiry check (Stale-While-Revalidate)
        // We use a coroutine scope to launch the check in parallel with emitting data
        // This ensures we show cached data immediately while refreshing in background if needed
        val scope = CoroutineScope(kotlinx.coroutines.Dispatchers.Default)
        scope.launch {
            checkCacheExpiry()
        }

        // Emit from DB based on current limit
        emitAll(
            currentLimit.flatMapLatest { limit ->
                localDataSource.getArticles(limit).map { entities ->
                    entities.map { it.toDomain() }
                }
            }
        )
    }

    // ... (existing refresh and loadNextPage methods) ...

    private suspend fun checkCacheExpiry() {
        val lastKey = localDataSource.getLastRemoteKey()
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val timeout = 60 * 60 * 1000L // 1 hour
        
        // If we have data, check if it's stale
        if (lastKey != null) {
            val isExpired = (currentTime - lastKey.createdAt > timeout)
            if (isExpired) {
                 refresh()
            }
        } else {
            // No data implies expired/empty, simple refresh
            refresh()
        }
    }

    override suspend fun refresh(): Result<Unit> {
        return try {
            // Reset limit
            currentLimit.value = 15
            
            // 1. Fetch from API (Key = null for first page)
            val response = remoteDataSource.fetchArticles(keyId = null)
            
            if (response.isSuccess && response.data != null) {
                transactionProvider.runAsTransaction {
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
                    val keys = response.data.mapIndexed { index, article ->
                        NewsRemoteKeysEntity(
                            articleId = article.id.toLong(),
                            prevKey = null, // First page has no prev
                            nextKey = response.pagination?.nextKeyId,
                            createdAt = Clock.System.now().toEpochMilliseconds(),
                            orderIndex = index // 0..14
                        )
                    }
                    
                    localDataSource.upsertArticles(articles)
                    localDataSource.upsertRemoteKeys(keys)
                }
                Result.Success(Unit)
            } else {
                Result.Error(AppException.UnknownError(errorMessage = response.errorMessage ?: "Unknown API Error"))
            }   
        } catch (e: Exception) {
            val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
            Result.Error(error)
        }
    }

    override suspend fun loadNextPage(): Result<Unit> {
        return try {
            val limit = currentLimit.value
            val dbCount = localDataSource.getCount()

            // If we have more data in DB than currently shown, just increase limit
            if (dbCount > limit) {
                currentLimit.value += 15
                return Result.Success(Unit)
            }

            // Otherwise, fetch from API
            val lastRemoteKey = localDataSource.getLastRemoteKey()
            val nextKey = lastRemoteKey?.nextKey

            if (nextKey == null) {
                 // End of list
                 return Result.Success(Unit)
            }

            val response = remoteDataSource.fetchArticles(keyId = nextKey)
            
            if (response.isSuccess && response.data != null) {
                transactionProvider.runAsTransaction {
                    val startOrderIndex = lastRemoteKey.orderIndex + 1
                    
                    val articles = response.data.map { it.toEntity(createdAt = Clock.System.now().toEpochMilliseconds()) }
                    val keys = response.data.mapIndexed { index, article ->
                        NewsRemoteKeysEntity(
                            articleId = article.id.toLong(),
                            prevKey = null, 
                            nextKey = response.pagination?.nextKeyId,
                            createdAt = Clock.System.now().toEpochMilliseconds(),
                            orderIndex = startOrderIndex + index
                        )
                    }
                    localDataSource.upsertArticles(articles)
                    localDataSource.upsertRemoteKeys(keys)
                }
                 // Increase limit to show new data
                currentLimit.value += 15
                Result.Success(Unit)
            } else {
                 Result.Error(AppException.UnknownError(errorMessage = response.errorMessage ?: "Unknown API Error"))
            }

        } catch (e: Exception) {
            val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
            Result.Error(error)
        }
    }


}
