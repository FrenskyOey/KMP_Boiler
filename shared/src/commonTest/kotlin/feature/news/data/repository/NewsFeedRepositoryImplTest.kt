package feature.news.data.repository

import core.domain.model.AppException
import core.domain.model.Result
import feature.news.data.model.ArticleDto
import feature.news.data.testhelper.FakeNewsLocalDataSource
import feature.news.data.testhelper.FakeNewsRemoteDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.toList

class NewsFeedRepositoryImplTest {

    private val fakeRemoteDataSource = FakeNewsRemoteDataSource()
    private val fakeLocalDataSource = FakeNewsLocalDataSource()
    private val repository = NewsFeedRepositoryImpl(fakeRemoteDataSource, fakeLocalDataSource)

    @Test
    fun `getArticles successfully fetches from remote, saves to local, and emits success`() = runTest {
        // Given
        val articleDto = ArticleDto(
            id = 1,
            title = "Title",
            content = "Content",
            imageUrl = "url",
            topic = "Topic",
            likes = 10,
            views = 100
        )
        fakeRemoteDataSource.success(listOf(articleDto))

        // When
        val results = repository.getArticles(1).toList()

        // Then
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        
        val successData = (results[1] as Result.Success).data
        assertEquals(1, successData.size)
        assertEquals("Title", successData[0].title)

        // Verify Local Save
        assertEquals(1, fakeLocalDataSource.savedArticles.size)
        assertEquals("Title", fakeLocalDataSource.savedArticles[0].title)
    }

    @Test
    fun `getArticles emits error when remote fails`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        fakeRemoteDataSource.failure(exception)

        // When
        val results = repository.getArticles(1).toList()

        // Then
        // Result.Loading -> Result.Error
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        
        val errorWithException = (results[1] as Result.Error).exception
        // ApiErrorHandler maps RuntimeException to Unknown base? or specific?
        // Checking just type
        assertTrue(errorWithException is AppException)
    }
}
