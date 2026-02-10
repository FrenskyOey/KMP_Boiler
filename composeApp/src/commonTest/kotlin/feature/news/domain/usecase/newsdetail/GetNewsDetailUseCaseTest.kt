package feature.news.domain.usecase.newsdetail

import core.domain.model.Result
import feature.news.domain.model.NewsDetail
import feature.news.domain.model.NewsContent
import feature.news.domain.repository.NewsDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import core.domain.model.AppException

class GetNewsDetailUseCaseTest {

    private val fakeRepository = FakeNewsDetailRepository()
    private val useCase = GetNewsDetailUseCase(fakeRepository)

    @Test
    fun `invoke returns success result from repository`() = runTest {
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
            shareUrl = "shareUrl"
        )
        fakeRepository.setReturnData(Result.Success(newsDetail))

        // When
        val result = useCase(1).single() // This will fail initially as implementation returns emptyFlow

        // Then
        assertTrue(result is Result.Success)
        assertEquals(newsDetail, result.data)
    }
}

class FakeNewsDetailRepository : NewsDetailRepository {
    private var result: Result<NewsDetail>? = null

    fun setReturnData(result: Result<NewsDetail>) {
        this.result = result
    }



    override fun getNewsDetail(id: Int): Flow<Result<NewsDetail>> {
        return if (result != null) {
            flowOf(result!!)
        } else {
            flowOf(Result.Error(AppException.UnknownError("No data set")))
        }
    }
}
