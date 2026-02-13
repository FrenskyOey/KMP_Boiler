package feature.news.domain.usecase.newsfeed

import core.domain.model.Result
import feature.news.data.repository.FakeNewsFeedRepository
import feature.news.domain.model.PaginationInfo
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RefreshNewsFeedUseCaseTest {

    private lateinit var repository: FakeNewsFeedRepository
    private lateinit var useCase: RefreshNewsFeedUseCase

    @BeforeTest
    fun setup() {
        repository = FakeNewsFeedRepository()
        useCase = RefreshNewsFeedUseCase(repository)
    }

    @Test
    fun `invoke should call repository refresh`() = runTest {
        // Given
        val paginationInfo = PaginationInfo(hasEndReached = false, currentLimit = 15)
        repository.refreshResult = Result.Success(paginationInfo)

        // When
        val result = useCase()

        // Then
        assertEquals(Result.Success(paginationInfo), result)
        assertTrue(repository.refreshCalled)
    }
}
