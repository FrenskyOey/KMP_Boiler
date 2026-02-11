package feature.news.domain.usecase.newsdetail

import core.domain.model.AppException
import core.domain.model.Result
import feature.news.domain.model.NewsDetail
import feature.news.domain.repository.NewsDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RefreshNewsDetailUseCaseTest {

    private val fakeRepository = FakeRefreshNewsDetailRepository()
    private val useCase = RefreshNewsDetailUseCase(fakeRepository)

    @Test
    fun `invoke calls repository refreshNewsDetail`() = runTest {
        // Given
        fakeRepository.setResult(Result.Success(Unit))

        // When
        val result = useCase(1)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(1, fakeRepository.lastRefreshedId)
    }

    @Test
    fun `invoke returns error from repository`() = runTest {
        // Given
        val error = Result.Error(AppException.NetworkError("Error"))
        fakeRepository.setResult(error)

        // When
        val result = useCase(1)

        // Then
        assertEquals(error, result)
    }
}

class FakeRefreshNewsDetailRepository : NewsDetailRepository {
    var lastRefreshedId: Int? = null
    private var result: Result<Unit> = Result.Success(Unit)

    fun setResult(newResult: Result<Unit>) {
        result = newResult
    }

    override fun getNewsDetail(id: Int): Flow<NewsDetail?> = emptyFlow()

    override suspend fun refreshNewsDetail(id: Int): Result<Unit> {
        lastRefreshedId = id
        return result
    }
}
