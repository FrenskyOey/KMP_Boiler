# CQS Repository Pattern for Kotlin Multiplatform

## Overview

Command-Query Separation (CQS) is a Repository pattern that separates **data observation** (Query) from **data fetching** (Command). This pattern is essential when the database is the source of truth and you need to prevent redundant network calls.

## When to Use CQS

### Decision Tree

```
Does Repository return Flow?
├─ YES → Is Database the source of truth?
│         ├─ YES → Use CQS Pattern ✅
│         └─ NO → Standard Repository
└─ NO → Standard Repository
```

### Use CQS Pattern When:
- ✅ Database is the source of truth
- ✅ Repository returns `Flow` that emits updates
- ✅ Data is fetched from remote and stored locally
- ✅ Need to separate observation from refresh action
- ✅ Multiple collectors should not trigger multiple fetches

**Examples**: News Detail, User Profile, Article List with caching

### Don't Use CQS When:
- ❌ Simple API call without local storage
- ❌ No Flow emissions needed
- ❌ Direct return of API response
- ❌ No database involvement

**Examples**: Login, Submit Form, One-time API calls

---

## Pattern Structure

### Repository Interface

```kotlin
interface NewsDetailRepository {
    // QUERY: Pure observation (no side effects)
    // Returns Flow from local database
    fun getNewsDetail(id: Int): Flow<NewsDetail?>
    
    // COMMAND: Explicit action (side effects allowed)
    // Fetches from remote, updates local database
    suspend fun refreshNewsDetail(id: Int): Result<Unit>
}
```

### Repository Implementation

```kotlin
class NewsDetailRepositoryImpl(
    private val remoteDataSource: NewsDetailDataSource.Remote,
    private val localDataSource: NewsDetailDataSource.Local
) : NewsDetailRepository {
    
    // QUERY: Only observe local data
    override fun getNewsDetail(id: Int): Flow<NewsDetail?> {
        return localDataSource.getNewsDetail(id)
            .map { it?.toDomain() }
    }
    
    // COMMAND: Fetch from network, update local
    override suspend fun refreshNewsDetail(id: Int): Result<Unit> {
        return try {
            val response = remoteDataSource.getNewsDetail(id)
            localDataSource.upsertNewsDetail(response.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
            Result.Error(error)
        }
    }
}
```

### Use Cases

```kotlin
// Query Use Case: Observation only
class GetNewsDetailUseCase(private val repository: NewsDetailRepository) {
    operator fun invoke(id: Int): Flow<NewsDetail?> {
        return repository.getNewsDetail(id)
    }
}

// Command Use Case: Refresh action
class RefreshNewsDetailUseCase(private val repository: NewsDetailRepository) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return repository.refreshNewsDetail(id)
    }
}
```

---

## Benefits

### Prevents Redundant Network Calls
- **Before CQS**: Every Flow collection triggers network fetch
- **After CQS**: Observation is cheap, fetch is explicit

### Explicit Control
- **Before CQS**: "Observing" has side effects (network call)
- **After CQS**: Clear separation between reading and updating

### Multiple Collectors
- **Before CQS**: 3 collectors = 3 network requests
- **After CQS**: 3 collectors = 1 Flow observation, 0 network calls

### Screen Rotation
- **Before CQS**: Rotation triggers redundant fetch
- **After CQS**: Rotation only re-observes local data

### Pull-to-Refresh
- **Before CQS**: Unclear when/how to trigger refresh
- **After CQS**: Explicit `refreshNewsDetail()` call

---

## Anti-Pattern: channelFlow with Side Effects

### ❌ Wrong: Combined Flow

```kotlin
override fun getNewsDetail(id: Int): Flow<Result<NewsDetail>> = channelFlow {
    // Observation
    launch {
        localDataSource.getNewsDetail(id)
            .filterNotNull()
            .map { Result.Success(it.toDomain()) }
            .collect { send(it) }
    }
    
    // Side effect: Network call on EVERY collection!
    try {
        val response = remoteDataSource.getNewsDetail(id)
        localDataSource.upsertNewsDetail(response.toEntity())
    } catch (e: Exception) {
        send(Result.Error(error))
    }
}
```

**Problems:**
- Every collection triggers network call
- Multiple collectors = multiple network requests
- Screen rotation = redundant fetch
- Difficult to test (UncompletedCoroutinesError)

### ✅ Correct: CQS Pattern

```kotlin
// Query: Pure observation
override fun getNewsDetail(id: Int): Flow<NewsDetail?> {
    return localDataSource.getNewsDetail(id)
        .map { it?.toDomain() }
}

// Command: Explicit fetch
override suspend fun refreshNewsDetail(id: Int): Result<Unit> {
    return try {
        val response = remoteDataSource.getNewsDetail(id)
        localDataSource.upsertNewsDetail(response.toEntity())
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(handleError(e))
    }
}
```

---

## Testing Strategy

### Query Operation Test

```kotlin
class GetNewsDetailUseCaseTest {
    private val fakeRepository = FakeGetNewsDetailRepository()
    private val useCase = GetNewsDetailUseCase(fakeRepository)

    @Test
    fun `invoke returns flow from repository`() = runTest {
        // Given
        val newsDetail = NewsDetail(...)
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

    override fun getNewsDetail(id: Int): Flow<NewsDetail?> = flowOf(data)
    override suspend fun refreshNewsDetail(id: Int): Result<Unit> = Result.Success(Unit)
}
```

### Command Operation Test

```kotlin
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
```

**Key Testing Principles:**
- One fake per test file (prevents naming conflicts)
- Operation-specific names: `FakeGetNewsDetailRepository`, `FakeRefreshNewsDetailRepository`
- Minimal implementation: Only what's needed for the test

---

## ViewModel Integration

```kotlin
class NewsDetailViewModel(
    private val getNewsDetail: GetNewsDetailUseCase,
    private val refreshNewsDetail: RefreshNewsDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<NewsDetailState>(NewsDetailState.Loading)
    val state: StateFlow<NewsDetailState> = _state.asStateFlow()

    fun loadNewsDetail(id: Int) {
        viewModelScope.launch {
            // Observe local data (cheap, no network call)
            getNewsDetail(id).collect { newsDetail ->
                _state.value = if (newsDetail != null) {
                    NewsDetailState.Success(newsDetail)
                } else {
                    NewsDetailState.Empty
                }
            }
        }
    }

    fun refresh(id: Int) {
        viewModelScope.launch {
            _state.value = NewsDetailState.Refreshing
            
            // Explicit network fetch
            when (val result = refreshNewsDetail(id)) {
                is Result.Success -> {
                    // Local data will update automatically via Flow
                }
                is Result.Error -> {
                    _state.value = NewsDetailState.Error(result.exception)
                }
            }
        }
    }
}
```

---

## Comparison: CQS vs Standard Repository

### CQS Pattern (Database as Source of Truth)

```kotlin
interface NewsDetailRepository {
    fun getNewsDetail(id: Int): Flow<NewsDetail?>
    suspend fun refreshNewsDetail(id: Int): Result<Unit>
}
```

**Use for**: News Detail, User Profile, Cached Lists

### Standard Pattern (Direct API Call)

```kotlin
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginModel>
}
```

**Use for**: Login, Submit Form, One-time API calls

---

## Key Takeaways

1. **Use CQS when database is source of truth**
2. **Separate observation (Flow) from action (suspend)**
3. **Prevents redundant network calls**
4. **Explicit control over when data refreshes**
5. **Perfect for Pull-to-Refresh scenarios**
6. **Avoid channelFlow with side effects**
7. **Test Query and Command operations separately**

---

## Related Patterns

- **Repository Pattern**: General data access abstraction
- **CQRS**: Command Query Responsibility Segregation (more complex, separate models)
- **Cache-First Strategy**: Always read from cache, update in background
- **Offline-First Architecture**: Local database as primary data source

---

## References

- [News Detail CQS Refactoring](file:///Users/frenskylee/.gemini/antigravity/brain/591ec534-76d2-4af5-879b-290e4d5789bf)
- [NewsDetailRepository.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/domain/repository/NewsDetailRepository.kt)
- [NewsDetailRepositoryImpl.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/repository/NewsDetailRepositoryImpl.kt)
