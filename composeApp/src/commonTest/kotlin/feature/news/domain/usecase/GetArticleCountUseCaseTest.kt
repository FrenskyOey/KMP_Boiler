package feature.news.domain.usecase

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetArticleCountUseCaseTest {

    private val fakeRepository = FakeNewsFeedRepository()
    private val useCase = GetArticleCountUseCase(fakeRepository)

    @Test
    fun `invoke returns count from repository`() = runTest {
        // Given
        val expectedCount = 5
        fakeRepository.setCountData(expectedCount)

        // When
        val result = useCase().single()

        // Then
        assertEquals(expectedCount, result)
    }
}
