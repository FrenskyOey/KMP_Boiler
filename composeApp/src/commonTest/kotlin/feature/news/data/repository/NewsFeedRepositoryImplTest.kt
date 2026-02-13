package feature.news.data.repository

import core.data.remote.model.BaseListResponse
import core.data.remote.model.Pagination
import core.domain.model.Result
import feature.news.data.testhelper.FakeNewsLocalDataSource
import feature.news.data.testhelper.FakeNewsRemoteDataSource
import feature.news.data.model.entity.ArticleEntity
import feature.news.data.model.entity.NewsRemoteKeysEntity
import feature.news.data.model.response.ArticleResponse
import feature.news.domain.model.Article
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NewsFeedRepositoryImplTest {

    private lateinit var remoteDataSource: FakeNewsRemoteDataSource
    private lateinit var localDataSource: FakeNewsLocalDataSource
    private lateinit var repository: NewsFeedRepositoryImpl

    @BeforeTest
    fun setup() {
        remoteDataSource = FakeNewsRemoteDataSource()
        localDataSource = FakeNewsLocalDataSource()
        repository = NewsFeedRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource
        )
    }

    @Test
    fun `refresh should fetch from API and update DB`() = runTest {
        // Given
        val mockData = listOf(
            ArticleResponse(1, "Title 1", "Content 1", "url1", "Topic 1"),
            ArticleResponse(2, "Title 2", "Content 2", "url2", "Topic 2")
        )
        val pagination = Pagination(nextKeyId = 2, isEndReached = false)
        remoteDataSource.fetchResult = BaseListResponse(data = mockData, isSuccess = true, pagination = pagination)

        // When
        val result = repository.refresh()

        // Then
        assertTrue(result is Result.Success, "Result should be Success")
        assertEquals(2, localDataSource.articlesState.value.size, "Article count mismatch")
        assertEquals(2, localDataSource.remoteKeys.size, "Keys count mismatch")
        // Verify keys
        val firstKey = localDataSource.remoteKeys.find { it.articleId == 1L }!!
        val secondKey = localDataSource.remoteKeys.find { it.articleId == 2L }!!
        
        assertEquals(2, firstKey.nextKey, "First key nextKey mismatch")
        assertEquals(0, firstKey.orderIndex, "First key orderIndex mismatch")
        assertEquals(1, secondKey.orderIndex, "Second key orderIndex mismatch")
    }

    @Test
    fun `loadNextPage should increase limit if DB has more data`() = runTest {
        // Given: DB has 20 items, default limit is 15
        val entities = (1..20).map { 
            ArticleEntity(it.toLong(), "Title $it", "Content $it", "url", "Topic", 0L) 
        }
        localDataSource.articlesState.value = entities
        // Add fresh keys to prevent checkCacheExpiry -> refresh() -> currentLimit reset
        localDataSource.remoteKeys.add(
            NewsRemoteKeysEntity(
                articleId = 1L, 
                prevKey = null, 
                nextKey = 2, 
                createdAt = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(), 
                orderIndex = 0
            )
        )

        // When
        val result = repository.loadNextPage()

        // Then
        assertTrue(result is Result.Success, "Result should be Success")
        // Verify via getArticles flow
        val articles = repository.getArticles().first()
        // If limit increased to 30 (15+15), we should see all 20 items. 
        assertEquals(20, articles.size, "Should return all 20 articles after limit increase")
    }
    
    @Test
    fun `loadNextPage should fetch API if DB exhausted`() = runTest {
        // Given: DB has 15 items. Limit is 15.
        // We set up specific Last Key to trigger API fetch
        val entities = (1..15).map { 
            ArticleEntity(it.toLong(), "Title $it", "Content $it", "url", "Topic", 0L) 
        }
        // Fake DAO insert manually
        localDataSource.articlesState.value = entities

        // Add remote key for the last item (id 15)
        localDataSource.remoteKeys.add(NewsRemoteKeysEntity(15L, null, 100, 0L, 14))

        val newArticles = listOf(
             ArticleResponse(16, "Title 16", "Content 16", "url", "Topic")
        )
        remoteDataSource.fetchResult = BaseListResponse(data = newArticles, isSuccess = true, pagination = Pagination(200, false))

        // When
        val result = repository.loadNextPage()

        // Then
        assertTrue(result is Result.Success, "Result should be Success")
        assertEquals(100, remoteDataSource.requestedKeyId, "Should request expected keyId")
        assertEquals(16, localDataSource.articlesState.value.size, "Should have 16 articles after fetch")
    }
}
