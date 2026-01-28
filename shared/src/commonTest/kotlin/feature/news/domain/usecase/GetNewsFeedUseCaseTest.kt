package feature.news.domain.usecase

import core.domain.model.Result
import core.domain.model.AppException
import feature.news.domain.model.Article
import feature.news.domain.repository.NewsFeedRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetNewsFeedUseCaseTest {

    private val fakeRepository = FakeNewsFeedRepository()
    private val useCase = GetNewsFeedUseCase(fakeRepository)

    @Test
    fun `invoke returns success result from repository`() = runTest {
        // Given
        val articles = listOf(
            Article(
                id = 1,
                title = "Test Article",
                content = "Content",
                imageUrl = "url",
                topic = "tech"
            )
        )
        fakeRepository.setReturnData(Result.Success(articles))

        // When
        val result = useCase(1).single()

        // Then
        assertTrue(result is Result.Success)
        assertEquals(articles, result.data)
    }

    @Test
    fun `invoke returns error result from repository`() = runTest {
        // Given
        val error = AppException.NetworkError("Network failure")
        fakeRepository.setReturnData(Result.Error(error))

        // When
        val result = useCase(1).single()

        // Then
        assertTrue(result is Result.Error)
        assertEquals(error, result.exception)
    }

    @Test
    fun `invoke passes correct page to repository`() = runTest {
        // Given
        fakeRepository.setReturnData(Result.Success(emptyList()))

        // When
        useCase(2).single()

        // Then
        assertEquals(2, fakeRepository.lastRequestedPage)
    }
}

class FakeNewsFeedRepository : NewsFeedRepository {
    private var result: Result<List<Article>> = Result.Success(emptyList())
    var lastRequestedPage: Int = -1

    private var count: Int = 0

    fun setReturnData(result: Result<List<Article>>) {
        this.result = result
    }

    fun setCountData(count: Int) {
        this.count = count
    }

    override fun getArticles(page: Int): Flow<Result<List<Article>>> {
        lastRequestedPage = page
        return flowOf(result)
    }
    
    override fun getArticleCount(): Flow<Int> = flowOf(count)
}
