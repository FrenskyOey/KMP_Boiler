package feature.news.ui.detail

import core.domain.model.Result
import feature.news.data.repository.FakeNewsDetailRepository
import feature.news.domain.model.NewsContent
import feature.news.domain.model.NewsDetail
import feature.news.domain.usecase.newsdetail.GetNewsDetailUseCase
import feature.news.domain.usecase.newsdetail.RefreshNewsDetailUseCase
import feature.news.ui.detail.state.NewsDetailEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@OptIn(ExperimentalCoroutinesApi::class)
class NewsDetailViewModelTest {

    private lateinit var repository: FakeNewsDetailRepository
    private lateinit var getNewsDetailUseCase: GetNewsDetailUseCase
    private lateinit var refreshNewsDetailUseCase: RefreshNewsDetailUseCase
    private lateinit var viewModel: NewsDetailViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeNewsDetailRepository()
        getNewsDetailUseCase = GetNewsDetailUseCase(repository)
        refreshNewsDetailUseCase = RefreshNewsDetailUseCase(repository)
        viewModel = NewsDetailViewModel(getNewsDetailUseCase, refreshNewsDetailUseCase)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `InitData should load detail and refresh`() = runTest(testDispatcher) {
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
        repository.refreshResult = Result.Success(Unit)

        // When
        viewModel.onEvent(NewsDetailEvent.InitData(1, "Test Title"))
        
        // Execute pending coroutines
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(detail, state.article)
        assertEquals(false, state.isLoading)
        assertEquals(null, state.error)
    }
}
