package feature.news.domain.usecase.newsfeed

import feature.news.data.repository.FakeNewsFeedRepository
import feature.news.domain.model.Article
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetNewsFeedUseCaseTest {

    private lateinit var repository: FakeNewsFeedRepository
    private lateinit var useCase: GetNewsFeedUseCase

    @BeforeTest
    fun setup() {
        repository = FakeNewsFeedRepository()
        useCase = GetNewsFeedUseCase(repository)
    }

    @Test
    fun `invoke should return flow from repository`() = runTest {
        // Given
        val articles = listOf(
            Article(
                id = 1,
                title = "Test Article",
                content = "Content",
                imageUrl = "url",
                topic = "TECH"
            )
        )
        repository.emitArticles(articles)

        // When
        val result = useCase().first()

        // Then
        assertEquals(articles, result)
    }
}
