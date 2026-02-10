package feature.news.data.repository

import core.domain.model.AppException
import core.domain.model.Result
import feature.news.data.datasource.NewsDetailDataSource
import feature.news.data.model.entity.ArticleDetailEntity
import feature.news.data.model.mapper.toDomain
import feature.news.data.model.mapper.toEntity
import feature.news.domain.model.NewsDetail
import feature.news.data.model.response.ArticleDetailResponse
import feature.news.data.model.response.AuthorResponse
import feature.news.data.model.response.ContentItemResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class NewsDetailRepositoryImplTest {

    private lateinit var repository: NewsDetailRepositoryImpl
    private lateinit var fakeRemoteDataSource: FakeNewsDetailRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeNewsDetailLocalDataSource

    @BeforeTest
    fun setup() {
        fakeRemoteDataSource = FakeNewsDetailRemoteDataSource()
        fakeLocalDataSource = FakeNewsDetailLocalDataSource()
        repository = NewsDetailRepositoryImpl(fakeRemoteDataSource, fakeLocalDataSource)
    }

    @Test
    fun `getNewsDetail emits local data`() = runTest {
        // Given
        val id = 1
        val localData = createResponse(id, "Local Title").toEntity()
        fakeLocalDataSource.flow.value = localData

        // When
        val result = repository.getNewsDetail(id).first()

        // Then
        assertEquals("Local Title", result?.title)
    }

    @Test
    fun `refreshNewsDetail fetches remote and updates local`() = runTest {
        // Given
        val id = 1
        val remoteData = createResponse(id, "Remote Title")
        fakeRemoteDataSource.response = remoteData

        // When
        val result = repository.refreshNewsDetail(id)

        // Then
        assertIs<Result.Success<*>>(result)
        assertEquals(remoteData.toEntity(), fakeLocalDataSource.storage[id])
    }
    
    @Test
    fun `refreshNewsDetail returns error when remote fails`() = runTest {
         // Given
        val id = 1
        fakeRemoteDataSource.exception = AppException.NetworkError("No connection")

        // When
        val result = repository.refreshNewsDetail(id)

        // Then
        assertIs<Result.Error>(result)
        assertIs<AppException.NetworkError>(result.exception)
    }

    // Helper classes
    class FakeNewsDetailRemoteDataSource : NewsDetailDataSource.Remote {
        var response: ArticleDetailResponse? = null
        var exception: Exception? = null

        override suspend fun getNewsDetail(id: Int): ArticleDetailResponse {
             exception?.let { throw it }
             return response!!
        }
    }

    class FakeNewsDetailLocalDataSource : NewsDetailDataSource.Local {
        val storage = mutableMapOf<Int, ArticleDetailEntity>()
        val flow = MutableStateFlow<ArticleDetailEntity?>(null)

        override fun getNewsDetail(id: Int): Flow<ArticleDetailEntity?> {
             return flow
        }

        override suspend fun upsertNewsDetail(article: ArticleDetailEntity) {
            storage[article.id] = article
            flow.value = article // Simulate Room Flow update
        }
    }

    private fun createResponse(id: Int, title: String = "Test Title") = ArticleDetailResponse(
        id = id,
        title = title,
        category = "Tech",
        image = "url",
        author = AuthorResponse("Name", "Avatar", "Pub"),
        publishedAt = "2024-01-01",
        readTime = 5,
        content = listOf(ContentItemResponse("paragraph", "Content")),
        tags = null, // Verify nullable handling
        shareUrl = "url"
    )
}
