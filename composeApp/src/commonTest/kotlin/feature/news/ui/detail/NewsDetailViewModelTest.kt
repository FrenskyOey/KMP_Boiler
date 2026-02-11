package feature.news.ui.detail

import core.domain.model.AppException
import core.domain.model.Result
import feature.news.domain.model.NewsDetail
import feature.news.domain.repository.NewsDetailRepository
import feature.news.domain.usecase.newsdetail.GetNewsDetailUseCase
import feature.news.domain.usecase.newsdetail.RefreshNewsDetailUseCase
import feature.news.ui.detail.state.NewsDetailEffect
import feature.news.ui.detail.state.NewsDetailEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NewsDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeNewsDetailRepository
    private lateinit var getUseCase: GetNewsDetailUseCase
    private lateinit var refreshUseCase: RefreshNewsDetailUseCase
    private lateinit var viewModel: NewsDetailViewModel

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeNewsDetailRepository()
        getUseCase = GetNewsDetailUseCase(fakeRepository)
        refreshUseCase = RefreshNewsDetailUseCase(fakeRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() {
        viewModel = NewsDetailViewModel(
            getNewsDetailUseCase = getUseCase,
            refreshNewsDetailUseCase = refreshUseCase
        )
    }

    @Test
    fun `initial state is empty`() = runTest(testDispatcher) {
        createViewModel()
        val state = viewModel.uiState.value
        assertEquals(0, state.articleId)
        assertEquals("", state.articleTitle)
        assertFalse(state.isLoading)
        assertEquals(null, state.article)
    }

    @Test
    fun `InitData event updates state and triggers refresh`() = runTest(testDispatcher) {
        // Given
        val mockArticle = NewsDetail(
            id = 1,
            title = "Test Title",
            category = "Tech",
            image = "url",
            author = NewsDetail.Author("Name", "Avatar", "Pub"),
            publishedAt = "Today",
            readTime = 5,
            content = emptyList(),
            tags = emptyList(),
            shareUrl = "share"
        )
        fakeRepository.setMockDetail(mockArticle)
        fakeRepository.setRefreshResult(Result.Success(Unit))
        createViewModel()

        // When
        viewModel.onEvent(NewsDetailEvent.InitData(1, "Test Title"))
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(1, state.articleId)
        assertEquals("Test Title", state.articleTitle)
        assertEquals(mockArticle, state.article)
        assertFalse(state.isLoading)
        assertEquals(1, fakeRepository.refreshCalledCount)
    }

    @Test
    fun `refresh failure with null article shows error page`() = runTest(testDispatcher) {
        // Given: No cached article and refresh fails
        fakeRepository.setMockDetail(null)
        fakeRepository.setRefreshResult(Result.Error(AppException.NetworkError("Network Error")))
        createViewModel()

        // When
        viewModel.onEvent(NewsDetailEvent.InitData(1, "Title"))
        advanceUntilIdle()

        // Then: Error state should be set (for error page)
        val state = viewModel.uiState.value
        assertEquals("Network Error", state.error)
        assertFalse(state.isLoading)
    }

    @Test
    fun `refresh failure with cached article shows snackbar`() = runTest(testDispatcher) {
        // Given: Cached article exists and refresh fails
        val mockArticle = NewsDetail(
            id = 1,
            title = "Cached Title",
            category = "Tech",
            image = "url",
            author = NewsDetail.Author("Name", "Avatar", "Pub"),
            publishedAt = "Today",
            readTime = 5,
            content = emptyList(),
            tags = emptyList(),
            shareUrl = "share"
        )
        fakeRepository.setMockDetail(mockArticle)
        fakeRepository.setRefreshResult(Result.Error(AppException.NetworkError("Network Error")))
        createViewModel()

        // Capture effects
        val effects = mutableListOf<NewsDetailEffect>()
        val job = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }

        // When
        viewModel.onEvent(NewsDetailEvent.InitData(1, "Title"))
        advanceUntilIdle()

        // Then: Error state should be null (no error page), and Snackbar effect emitted
        val state = viewModel.uiState.value
        assertNull(state.error) 
        assertEquals(mockArticle, state.article)
        assertFalse(state.isLoading)
        
        assertTrue(effects.any { it is NewsDetailEffect.ShowSnackbar && it.message == "Network Error" })
        
        job.cancel()
    }

    @Test
    fun `late database emission clears error state`() = runTest(testDispatcher) {
        // Given: Network fails fast and sets error
        fakeRepository.setMockDetail(null) // Initially empty DB
        fakeRepository.setRefreshResult(Result.Error(AppException.NetworkError("Network Error")))
        createViewModel()

        viewModel.onEvent(NewsDetailEvent.InitData(1, "Title"))
        advanceUntilIdle()
        
        // Confirm error state
        assertEquals("Network Error", viewModel.uiState.value.error)

        // When: DB emits data later
        val mockArticle = NewsDetail(
            id = 1,
            title = "Late Title",
            category = "Tech",
            image = "url",
            author = NewsDetail.Author("Name", "Avatar", "Pub"),
            publishedAt = "Today",
            readTime = 5,
            content = emptyList(),
            tags = emptyList(),
            shareUrl = "share"
        )
        fakeRepository.emitLateDetail(mockArticle)
        advanceUntilIdle()

        // Then: Article is set AND Error is cleared
        val state = viewModel.uiState.value
        assertEquals(mockArticle, state.article)
        assertNull(state.error)
    }
}

class FakeNewsDetailRepository : NewsDetailRepository {
    private val _detailFlow = MutableStateFlow<NewsDetail?>(null)
    private var refreshResult: Result<Unit> = Result.Success(Unit)
    var refreshCalledCount = 0

    fun setMockDetail(detail: NewsDetail?) {
        _detailFlow.update { detail }
    }

    fun emitLateDetail(detail: NewsDetail) {
        _detailFlow.update { detail }
    }

    fun setRefreshResult(result: Result<Unit>) {
        refreshResult = result
    }

    override fun getNewsDetail(id: Int): Flow<NewsDetail?> {
        return _detailFlow
    }

    override suspend fun refreshNewsDetail(id: Int): Result<Unit> {
        refreshCalledCount++
        return refreshResult
    }
}
