package feature.news.domain.usecase.newsdetail

import feature.news.domain.model.NewsDetail
import feature.news.domain.model.NewsContent
import feature.news.domain.repository.NewsDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import core.domain.model.Result

class GetNewsDetailUseCaseTest {

    private val fakeRepository = FakeGetNewsDetailRepository()
    private val useCase = GetNewsDetailUseCase(fakeRepository)

    @Test
    fun `invoke returns flow from repository`() = runTest {
        // Given
        val newsDetail = NewsDetail(
            id = 1,
            title = "Test Article",
            category = "Tech",
            image = "url",
            author = NewsDetail.Author("Author", "Avatar", "Pub"),
            publishedAt = "2024-01-01",
            readTime = 5,
            content = listOf(
                NewsContent.Paragraph("Content"),
                NewsContent.Quote(text = "Quote text", highlighted = true, emphasis = true)
            ),
            tags = null,
            shareUrl = "shareUrl"
        )
        fakeRepository.setData(newsDetail)

        // When
        val result = useCase(1).single()

        // Then
        assertEquals(newsDetail, result)
    }
}

class FakeGetNewsDetailRepository : NewsDetailRepository {
    private var data: NewsDetail? = null

    fun setData(newsDetail: NewsDetail?) {
        this.data = newsDetail
    }

    override fun getNewsDetail(id: Int): Flow<NewsDetail?> {
        return flowOf(data)
    }

    override suspend fun refreshNewsDetail(id: Int): Result<Unit> {
        return Result.Success(Unit)
    }
}
