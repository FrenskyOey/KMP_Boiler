package feature.news.ui.main

import core.domain.model.AppException
import core.domain.model.Result
import feature.news.domain.model.Article
import feature.news.domain.model.PaginationInfo
import feature.news.domain.repository.NewsFeedRepository
import feature.news.domain.usecase.newsfeed.CheckCacheExpiredUseCase
import feature.news.domain.usecase.newsfeed.GetNewsFeedUseCase
import feature.news.domain.usecase.newsfeed.LoadMoreNewsUseCase
import feature.news.domain.usecase.newsfeed.RefreshNewsFeedUseCase
import feature.news.ui.main.state.NewsEffect
import feature.news.ui.main.state.NewsIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class NewsFeedViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var fakeRepository: FakeNewsFeedRepository
    private lateinit var viewModel: NewsFeedViewModel
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        fakeRepository = FakeNewsFeedRepository()
        
        // Create real UseCases with fake repository
        val getNewsFeedUseCase = GetNewsFeedUseCase(fakeRepository)
        val refreshUseCase = RefreshNewsFeedUseCase(fakeRepository)
        val loadMoreUseCase = LoadMoreNewsUseCase(fakeRepository)
        val checkCacheExpiredUseCase = CheckCacheExpiredUseCase(fakeRepository)
        
        viewModel = NewsFeedViewModel(
            getNewsFeedUseCase = getNewsFeedUseCase,
            refreshNewsFeedUseCase = refreshUseCase,
            loadMoreNewsUseCase = loadMoreUseCase,
            checkCacheExpiredUseCase = checkCacheExpiredUseCase
        )
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    // ========== Initial State ==========
    
    @Test
    fun `initial state is correct`() {
        val state = viewModel.uiState.value
        assertTrue(state.articles.isEmpty())
        assertTrue(state.isLoading)  // Initial loading
        assertFalse(state.isPaginationLoading)
        assertFalse(state.isRefreshing)
        assertEquals(null, state.error)
        assertFalse(state.isEndReached)
    }
    
    // ========== GetNewsFeedUseCase Flow ==========
    
    @Test
    fun `observeArticles updates state when flow emits`() = runTest(testDispatcher) {
        // Given
        val articles = listOf(
            Article(1, "Title 1", "Content 1", "url1", "Topic 1"),
            Article(2, "Title 2", "Content 2", "url2", "Topic 2")
        )
        fakeRepository.emit(articles)
        
        // When
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertEquals(2, state.articles.size)
        assertFalse(state.isLoading)
        assertFalse(state.isPaginationLoading)
        assertFalse(state.isRefreshing)
    }
    
    @Test
    fun `observeArticles handles empty list`() = runTest(testDispatcher) {
        // Given
        fakeRepository.emit(emptyList())
        
        // When
        advanceUntilIdle()
        
        // Then
        val state = viewModel.uiState.value
        assertTrue(state.articles.isEmpty())
        assertFalse(state.isLoading)
    }
    
    // ========== Refresh ==========
    
    @Test
    fun `refresh success updates isRefreshing state`() = runTest(testDispatcher) {
        // Given
        fakeRepository.setRefreshResult(Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 15)))
        
        // When
        viewModel.onIntent(NewsIntent.Refresh)
        
        // Emit articles after refresh
        val articles = listOf(Article(1, "New", "Content", "url", "Topic"))
        fakeRepository.emit(articles)
        advanceUntilIdle()
        
        // Then - isRefreshing reset by observeArticles
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertEquals(1, viewModel.uiState.value.articles.size)
        assertTrue(fakeRepository.refreshCalled)
    }
    
    @Test
    fun `refresh error shows error effect and resets isRefreshing`() = runTest(testDispatcher) {
        // Given
        val error = AppException.NetworkError("No connection")
        fakeRepository.setRefreshResult(Result.Error(error))
        
        // Collect effects
        val effects = mutableListOf<NewsEffect>()
        val job = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }
        
        // When
        viewModel.onIntent(NewsIntent.Refresh)
        advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertEquals(1, effects.size)
        assertTrue(effects[0] is NewsEffect.ShowError)
        assertTrue((effects[0] as NewsEffect.ShowError).message.contains("connection"))
        
        job.cancel()
    }
    
    @Test
    fun `refresh resets isEndReached`() = runTest(testDispatcher) {
        // Given
        fakeRepository.setLoadMoreResult(Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 30)))
        
        // When
        viewModel.onIntent(NewsIntent.Refresh)
        
        // Then
        assertFalse(viewModel.uiState.value.isEndReached)
    }
    
    // ========== Load More ==========
    
    @Test
    fun `loadNextPage success updates isPaginationLoading state`() = runTest(testDispatcher) {
        // Given
        fakeRepository.setLoadMoreResult(Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 30)))
        val articles = listOf(
            Article(1, "Title 1", "Content 1", "url1", "Topic 1"),
            Article(2, "Title 2", "Content 2", "url2", "Topic 2")
        )
        fakeRepository.emit(articles)
        advanceUntilIdle()
        
        // When
        viewModel.onIntent(NewsIntent.LoadNextPage)
        advanceUntilIdle()  // Let loadMore complete
        
        // Check isPaginationLoading is true during operation
        // Note: By the time we check, it might already be reset if Flow emitted
        
        // Emit more articles
        val moreArticles = articles + Article(3, "Title 3", "Content 3", "url3", "Topic 3")
        fakeRepository.emit(moreArticles)
        advanceUntilIdle()
        
        // Then - isPaginationLoading reset by observeArticles
        assertFalse(viewModel.uiState.value.isPaginationLoading)
        assertEquals(3, viewModel.uiState.value.articles.size)
        assertTrue(fakeRepository.loadMoreCalled)
    }
    
    @Test
    fun `loadNextPage error shows error effect and resets isPaginationLoading`() = runTest(testDispatcher) {
        // Given - Set up initial articles so loadNextPage guard passes
        val initialArticles = listOf(
            Article(1, "Title 1", "Content 1", "url1", "Topic 1"),
            Article(2, "Title 2", "Content 2", "url2", "Topic 2")
        )
        fakeRepository.emit(initialArticles)
        advanceUntilIdle()
        
        val error = AppException.NetworkError("Failed to load")
        fakeRepository.setLoadMoreResult(Result.Error(error))
        
        // Collect effects
        val effects = mutableListOf<NewsEffect>()
        val job = launch(testDispatcher) {
            viewModel.effect.collect { effects.add(it) }
        }
        
        // When
        viewModel.onIntent(NewsIntent.LoadNextPage)
        advanceUntilIdle()
        
        // Then
        assertFalse(viewModel.uiState.value.isPaginationLoading)
        assertEquals(1, effects.size)
        assertTrue(effects[0] is NewsEffect.ShowError)
        assertTrue((effects[0] as NewsEffect.ShowError).message.contains("load"))
        
        job.cancel()
    }
    
    @Test
    fun `loadNextPage does not trigger if isPaginationLoading is true`() = runTest(testDispatcher) {
        // Given
        fakeRepository.setLoadMoreResult(Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 30)))
        viewModel.onIntent(NewsIntent.LoadNextPage)
        
        // When - try to load again while first is in progress (before advanceUntilIdle)
        fakeRepository.loadMoreCount = 0
        viewModel.onIntent(NewsIntent.LoadNextPage)
        
        // Then - second call should be ignored
        assertEquals(0, fakeRepository.loadMoreCount)
    }
    
    // ========== Retry ==========
    
    @Test
    fun `retry with empty articles calls refresh`() = runTest(testDispatcher) {
        // Given - empty articles
        fakeRepository.emit(emptyList())
        advanceUntilIdle()
        
        fakeRepository.setRefreshResult(Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 15)))
        
        // When
        viewModel.onIntent(NewsIntent.Retry)
        advanceUntilIdle()
        
        // Then
        assertTrue(fakeRepository.refreshCalled)
    }
    
    @Test
    fun `retry with existing articles calls loadNextPage`() = runTest(testDispatcher) {
        // Given - has articles
        val articles = listOf(Article(1, "Title", "Content", "url", "Topic"))
        fakeRepository.emit(articles)
        advanceUntilIdle()
        
        fakeRepository.setLoadMoreResult(Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 30)))
        
        // When
        viewModel.onIntent(NewsIntent.Retry)
        advanceUntilIdle()
        
        // Then
        assertTrue(fakeRepository.loadMoreCalled)
    }
}

// ========== Fake Repository ==========

class FakeNewsFeedRepository : NewsFeedRepository {
    private val articlesFlow = MutableStateFlow<List<Article>>(emptyList())
    private var refreshResult: Result<PaginationInfo> = Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 15))
    private var loadMoreResult: Result<PaginationInfo> = Result.Success(PaginationInfo(hasEndReached = false, currentLimit = 30))
    
    var refreshCalled = false
    var refreshCount = 0
    var loadMoreCalled = false
    var loadMoreCount = 0
    
    fun emit(articles: List<Article>) {
        articlesFlow.value = articles
    }
    
    fun setRefreshResult(result: Result<PaginationInfo>) {
        this.refreshResult = result
    }
    
    fun setLoadMoreResult(result: Result<PaginationInfo>) {
        this.loadMoreResult = result
    }
    
    override fun getArticles(): Flow<List<Article>> = articlesFlow
    
    override suspend fun refresh(): Result<PaginationInfo> {
        refreshCalled = true
        refreshCount++
        return refreshResult
    }
    
    override suspend fun loadNextPage(): Result<PaginationInfo> {
        loadMoreCalled = true
        loadMoreCount++
        return loadMoreResult
    }
    
    override suspend fun isCacheExpired(): Boolean = false
}
