package feature.news.domain.usecase.newsdetail

import feature.news.data.repository.FakeNewsDetailRepository
import feature.news.domain.model.NewsContent
import feature.news.domain.model.NewsDetail
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetNewsDetailUseCaseTest {

    private lateinit var repository: FakeNewsDetailRepository
    private lateinit var useCase: GetNewsDetailUseCase

    @BeforeTest
    fun setup() {
        repository = FakeNewsDetailRepository()
        useCase = GetNewsDetailUseCase(repository)
    }

    @Test
    fun `invoke should return flow from repository`() = runTest {
        // Given
        val detail = NewsDetail(
            id = 1,
            title = "Test Detail",
            category = "TECH",
            image = "url",
            author = NewsDetail.Author("Author", "avatar", "pub"),
            publishedAt = "2024-01-01",
            readTime = 5,
            content = listOf(NewsContent.Paragraph("Content")),
            tags = listOf("Tag"),
            shareUrl = "share"
        )
        repository.emitNewsDetail(1, detail)

        // When
        val result = useCase(1).first()

        // Then
        assertEquals(detail, result)
    }
}
