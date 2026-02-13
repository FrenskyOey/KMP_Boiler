# Offline-First Pagination Guide

This guide documents the offline-first pagination pattern implemented in the News Feed feature. Use this as a reference when implementing similar pagination in other features.

---

## Overview

This pattern provides:
- **Offline-First**: Data loads instantly from local cache
- **Key-Based Pagination**: Server-driven pagination using `nextKeyId`
- **Stale-While-Revalidate**: Shows cached data immediately while refreshing in background
- **Smart Invalidation**: Preserves cache when possible, clears only when data chain breaks
- **Manual Limit Control**: Efficient pagination without Paging 3 library

---

## Architecture Components

### 1. Database Entities

#### ArticleEntity
```kotlin
@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val content: String,
    val imageUrl: String,
    val topic: String,
    val createdAt: Long  // Timestamp for cache expiry
)
```

#### NewsRemoteKeysEntity
```kotlin
@Entity(tableName = "news_remote_keys")
data class NewsRemoteKeysEntity(
    @PrimaryKey val articleId: Long,
    val prevKey: Int?,
    val nextKey: Int?,       // Key for next page
    val createdAt: Long,     // Timestamp for cache expiry
    val orderIndex: Int      // Preserves API order
)
```

**Why Remote Keys?**
- Tracks pagination state per item
- Maintains correct order from API via `orderIndex`
- Enables smart cache invalidation
- Supports bidirectional pagination (future)

### 2. API Response

```kotlin
@Serializable
data class BaseListResponse<T>(
    val data: List<T>?,
    val isSuccess: Boolean,
    val errorMessage: String?,
    val pagination: Pagination?
)

@Serializable
data class Pagination(
    val nextKeyId: Int?,      // Key for next page (null = end)
    val isEndReached: Boolean
)
```

### 3. Repository Pattern

```kotlin
interface NewsFeedRepository {
    fun getArticles(): Flow<List<Article>>  // Query: Observe cache
    suspend fun refresh(): Result<Unit>      // Command: Refresh first page
    suspend fun loadNextPage(): Result<Unit> // Command: Load more
}
```

---

## Implementation Flow

### Initial Load (First Time)

```mermaid
sequenceDiagram
    participant UI
    participant Repo
    participant Cache
    participant API

    UI->>Repo: getArticles()
    Repo->>Cache: Query articles (limit=15)
    Cache-->>Repo: Empty list
    Repo->>UI: Emit empty list
    
    Note over Repo: Background: checkCacheExpiry()
    Repo->>Cache: getLastRemoteKey()
    Cache-->>Repo: null (no data)
    Repo->>API: fetchArticles(keyId=null)
    API-->>Repo: {data: [1..15], nextKeyId: 15}
    Repo->>Cache: Upsert articles + keys
    Cache-->>UI: Emit 15 articles (auto-update)
```

### Subsequent Load (Cache Hit)

```mermaid
sequenceDiagram
    participant UI
    participant Repo
    participant Cache
    participant API

    UI->>Repo: getArticles()
    Repo->>Cache: Query articles (limit=15)
    Cache-->>Repo: 15 cached articles
    Repo->>UI: Emit 15 articles (instant)
    
    Note over Repo: Background: checkCacheExpiry()
    Repo->>Cache: getLastRemoteKey()
    Cache-->>Repo: {createdAt: recent}
    Note over Repo: Cache fresh, skip refresh
```

### Load Next Page

```mermaid
sequenceDiagram
    participant UI
    participant Repo
    participant Cache
    participant API

    UI->>Repo: loadNextPage()
    Repo->>Cache: getCount()
    Cache-->>Repo: 15 items
    Note over Repo: DB exhausted (count=limit)
    
    Repo->>Cache: getLastRemoteKey()
    Cache-->>Repo: {nextKey: 15, orderIndex: 14}
    Repo->>API: fetchArticles(keyId=15)
    API-->>Repo: {data: [16..30], nextKeyId: 30}
    Repo->>Cache: Upsert articles + keys
    Repo->>Repo: currentLimit += 15
    Cache-->>UI: Emit 30 articles (auto-update)
```

---

## Key Mechanisms

### 1. Stale-While-Revalidate

```kotlin
override fun getArticles(): Flow<List<Article>> = flow {
    // Launch background cache check (non-blocking)
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launch {
        checkCacheExpiry()  // Refreshes if >1 hour old
    }

    // Immediately emit cached data
    emitAll(
        currentLimit.flatMapLatest { limit ->
            localDataSource.getArticles(limit).map { it.toDomain() }
        }
    )
}
```

**Benefits:**
- UI shows data instantly
- Background refresh updates cache
- No loading spinners on subsequent visits

### 2. Smart Invalidation

```kotlin
override suspend fun refresh(): Result<Unit> {
    val response = remoteDataSource.fetchArticles(keyId = null)
    
    if (response.isSuccess && response.data != null) {
        transactionProvider.runAsTransaction {
            val firstId = response.data.first().id
            val firstKey = localDataSource.getRemoteKeys(firstId)
            val isSameChain = firstKey?.nextKey == response.pagination?.nextKeyId
            
            if (!isSameChain) {
                // Chain broken: clear keys only (articles preserved via upsert)
                localDataSource.clearRemoteKeys()
            }
            
            // Upsert new data
            localDataSource.upsertArticles(articles)
            localDataSource.upsertRemoteKeys(keys)
        }
    }
}
```

**Why Clear Keys Only?**
- Articles are upserted (replaced if ID exists)
- Keys determine pagination state
- Clearing keys resets pagination without losing article data

### 3. Efficient Pagination

```kotlin
override suspend fun loadNextPage(): Result<Unit> {
    val limit = currentLimit.value
    val dbCount = localDataSource.getCount()

    // Optimization: If DB has more data, just increase limit
    if (dbCount > limit) {
        currentLimit.value += 15
        return Result.Success(Unit)  // Instant load!
    }

    // Otherwise, fetch from API
    val lastKey = localDataSource.getLastRemoteKey()
    val nextKey = lastKey?.nextKey ?: return Result.Success(Unit)  // End reached
    
    val response = remoteDataSource.fetchArticles(keyId = nextKey)
    // ... upsert and increase limit
}
```

**Benefits:**
- Instant "load more" if data already cached
- Only fetches when truly needed
- Supports offline browsing of cached pages

---

## Use Cases

### Use Case 1: Normal Pagination

**Scenario:** User scrolls through news feed

1. **Initial Load**
   - User opens app
   - `getArticles()` emits 15 cached articles instantly
   - Background: `checkCacheExpiry()` refreshes if >1 hour old

2. **Scroll to Bottom**
   - UI calls `loadNextPage()`
   - Repository checks: DB has 15, limit is 15 → fetch from API
   - Fetches articles 16-30 using `nextKeyId: 15`
   - Increases limit to 30
   - Flow emits 30 articles

3. **Continue Scrolling**
   - UI calls `loadNextPage()` again
   - Fetches articles 31-45 using `nextKeyId: 30`
   - Limit becomes 45

4. **End Reached**
   - API returns `nextKeyId: null`
   - `loadNextPage()` returns success without fetching
   - UI shows "No more items"

### Use Case 2: Pull-to-Refresh

**Scenario:** User manually refreshes

1. **User Pulls Down**
   - UI calls `refresh()`
   - Fetches first page from API (keyId = null)
   - Smart invalidation: checks if chain matches
   - If same: upserts new data, preserves pagination
   - If different: clears keys, resets pagination
   - Limit resets to 15

2. **Result**
   - Fresh data displayed
   - Pagination state reset
   - User can scroll again

### Use Case 3: Offline Browsing

**Scenario:** User has no internet

1. **Open App**
   - `getArticles()` emits 45 cached articles (from previous session)
   - Background: `checkCacheExpiry()` tries to refresh → fails silently
   - User sees cached data

2. **Scroll to Bottom**
   - UI calls `loadNextPage()`
   - DB has 45 items, limit is 45 → fetch from API
   - API call fails (no internet)
   - Returns `Result.Error(NetworkException)`
   - UI shows error toast
   - User can still browse cached 45 articles

3. **Change Limit Manually**
   - If DB had 60 items but limit was 45
   - `loadNextPage()` would increase limit to 60 instantly
   - No API call needed!

---

## Edge Cases

### Edge Case 1: Cache Expired

**Scenario:** User opens app after 2 hours

```kotlin
// checkCacheExpiry() logic
val lastKey = localDataSource.getLastRemoteKey()
val currentTime = Clock.System.now().toEpochMilliseconds()
val timeout = 60 * 60 * 1000L  // 1 hour

if (lastKey != null) {
    val isExpired = (currentTime - lastKey.createdAt > timeout)
    if (isExpired) {
        refresh()  // Background refresh
    }
}
```

**Flow:**
1. User opens app
2. `getArticles()` emits cached data instantly
3. Background: `checkCacheExpiry()` detects expiry
4. Calls `refresh()` to fetch fresh data
5. Cache updates, Flow emits new data
6. UI updates smoothly without blocking

### Edge Case 2: API Error During Refresh

**Scenario:** Network error while refreshing

```kotlin
override suspend fun refresh(): Result<Unit> {
    return try {
        val response = remoteDataSource.fetchArticles(keyId = null)
        // ... process response
    } catch (e: Exception) {
        val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
        Result.Error(error)  // Return error, cache unchanged
    }
}
```

**Flow:**
1. User pulls to refresh
2. API call fails (timeout, no internet, etc.)
3. `refresh()` returns `Result.Error`
4. UI shows error message
5. **Cache remains intact** → user still sees old data
6. User can retry later

### Edge Case 3: Pagination Chain Broken

**Scenario:** Backend data changed (e.g., new articles inserted at top)

**Before:**
- Cache: Articles 1-30
- Keys: `{1: nextKey=15}, {16: nextKey=30}`

**After Backend Update:**
- API now returns: Articles 5-20 for first page
- `nextKeyId` changed from 15 to 20

**Flow:**
```kotlin
val firstId = response.data.first().id  // 5
val firstKey = localDataSource.getRemoteKeys(5)  // null or different nextKey
val isSameChain = firstKey?.nextKey == response.pagination?.nextKeyId  // false

if (!isSameChain) {
    localDataSource.clearRemoteKeys()  // Reset pagination
}
```

**Result:**
1. Detects chain break
2. Clears remote keys (resets pagination)
3. Upserts new articles (IDs 5-20 replace/add to cache)
4. User sees updated data
5. Pagination starts fresh

### Edge Case 4: Empty Response

**Scenario:** API returns empty list

```kotlin
if (response.isSuccess && response.data != null) {
    // Process data
} else {
    Result.Error(AppException.UnknownError("Unknown API Error"))
}
```

**Flow:**
1. API returns `{data: [], isSuccess: true}`
2. Condition fails (data is empty list, not null, but check passes)
3. Transaction runs with empty lists
4. No articles/keys inserted
5. Cache unchanged
6. User sees existing cached data

**Note:** Consider adding explicit empty check if needed:
```kotlin
if (response.isSuccess && !response.data.isNullOrEmpty()) {
    // Process
}
```

### Edge Case 5: Concurrent Refresh and LoadMore

**Scenario:** User scrolls while refresh is happening

**Problem:** Race condition
- Thread 1: `refresh()` → resets limit to 15
- Thread 2: `loadNextPage()` → increases limit to 30
- Result: Inconsistent state

**Solution:** Use `MutableStateFlow` for limit
```kotlin
private val currentLimit = MutableStateFlow(15)
```

**Benefits:**
- Thread-safe updates
- Flow collectors get latest value
- Automatic UI updates

**Recommendation:** In production, add mutex:
```kotlin
private val paginationMutex = Mutex()

override suspend fun refresh(): Result<Unit> {
    paginationMutex.withLock {
        // ... refresh logic
    }
}
```

### Edge Case 6: Database Transaction Failure

**Scenario:** Room transaction fails mid-operation

```kotlin
transactionProvider.runAsTransaction {
    localDataSource.clearRemoteKeys()
    localDataSource.upsertArticles(articles)
    localDataSource.upsertRemoteKeys(keys)  // Fails here
}
```

**Flow:**
1. Transaction starts
2. Keys cleared successfully
3. Articles upserted successfully
4. Keys upsert fails (e.g., constraint violation)
5. **Entire transaction rolls back**
6. Database unchanged
7. Exception propagated to repository
8. Returns `Result.Error`

**Benefits of Transactions:**
- All-or-nothing guarantee
- No partial updates
- Data consistency maintained

---

## Implementation Checklist

When implementing this pattern in a new feature:

### Database Layer
- [ ] Create Entity for main data (e.g., `ProductEntity`)
- [ ] Create RemoteKeysEntity with:
  - [ ] `@PrimaryKey` matching main entity ID
  - [ ] `prevKey: Int?` (for bidirectional pagination)
  - [ ] `nextKey: Int?` (for next page)
  - [ ] `createdAt: Long` (for cache expiry)
  - [ ] `orderIndex: Int` (for API order)
- [ ] Create DAO with:
  - [ ] `getItems(limit: Int): Flow<List<Entity>>`
  - [ ] `@Insert(onConflict = REPLACE)` for upsert
  - [ ] `clearAll()` for main table
  - [ ] `getCount(): Int`
- [ ] Create RemoteKeysDao with:
  - [ ] `getRemoteKeys(id: Long): RemoteKeysEntity?`
  - [ ] `getLastRemoteKey(): RemoteKeysEntity?`
  - [ ] `@Insert(onConflict = REPLACE)`
  - [ ] `clearRemoteKeys()`

### Data Layer
- [ ] Create DataSource interfaces (Remote/Local)
- [ ] Implement RemoteDataSource with API calls
- [ ] Implement LocalDataSource delegating to DAOs
- [ ] Create Repository with:
  - [ ] `getItems(): Flow<List<DomainModel>>`
  - [ ] `refresh(): Result<Unit>`
  - [ ] `loadNextPage(): Result<Unit>`
  - [ ] `private val currentLimit = MutableStateFlow(15)`
  - [ ] `private suspend fun checkCacheExpiry()`

### Domain Layer
- [ ] Create Repository interface
- [ ] Create UseCases:
  - [ ] `GetItemsUseCase`
  - [ ] `RefreshItemsUseCase`
  - [ ] `LoadMoreItemsUseCase`

### UI Layer
- [ ] ViewModel observes `getItems()` Flow
- [ ] Implement pull-to-refresh calling `refresh()`
- [ ] Implement scroll listener calling `loadNextPage()`
- [ ] Handle loading states (initial, pagination, refresh)
- [ ] Handle error states with retry

---

## Testing Strategy

### Repository Tests

```kotlin
@Test
fun `refresh should fetch from API and update DB`() = runTest {
    // Given
    val mockData = listOf(/* ... */)
    remoteDataSource.fetchResult = BaseListResponse(data = mockData, ...)

    // When
    val result = repository.refresh()

    // Then
    assertTrue(result is Result.Success)
    assertEquals(2, localDataSource.articlesState.value.size)
    assertEquals(2, localDataSource.remoteKeys.size)
}

@Test
fun `loadNextPage should increase limit if DB has more data`() = runTest {
    // Given: DB has 20 items, limit is 15
    localDataSource.articlesState.value = (1..20).map { /* ... */ }
    localDataSource.remoteKeys.add(/* fresh key to prevent auto-refresh */)

    // When
    repository.loadNextPage()

    // Then
    val articles = repository.getArticles().first()
    assertEquals(20, articles.size)  // All items visible
}

@Test
fun `loadNextPage should fetch API if DB exhausted`() = runTest {
    // Given: DB has 15 items, limit is 15
    localDataSource.articlesState.value = (1..15).map { /* ... */ }
    localDataSource.remoteKeys.add(NewsRemoteKeysEntity(15L, null, 100, 0L, 14))
    remoteDataSource.fetchResult = BaseListResponse(/* new data */)

    // When
    repository.loadNextPage()

    // Then
    assertEquals(100, remoteDataSource.requestedKeyId)  // Correct key used
    assertEquals(16, localDataSource.articlesState.value.size)  // New data added
}
```

---

## Performance Considerations

### Memory
- **Limit Control**: `currentLimit` prevents loading entire DB into memory
- **Flow Emissions**: Only emits when limit changes or data updates
- **Recommendation**: Cap max limit (e.g., 300 items) to prevent OOM

### Database
- **Indexes**: Add index on `orderIndex` for fast sorting
  ```kotlin
  @Entity(
      tableName = "news_remote_keys",
      indices = [Index(value = ["orderIndex"])]
  )
  ```
- **Query Optimization**: Join query in DAO is efficient
  ```sql
  SELECT * FROM articles 
  INNER JOIN news_remote_keys ON articles.id = news_remote_keys.articleId 
  ORDER BY news_remote_keys.orderIndex ASC 
  LIMIT :limit
  ```

### Network
- **Batching**: Fetch 15 items per page (configurable)
- **Caching**: 1-hour expiry reduces API calls
- **Error Handling**: Graceful degradation on network errors

---

## Common Pitfalls

### 1. Forgetting to Add Fresh Keys in Tests
**Problem:** `checkCacheExpiry()` triggers `refresh()` in tests, resetting limit

**Solution:**
```kotlin
localDataSource.remoteKeys.add(
    NewsRemoteKeysEntity(
        articleId = 1L,
        prevKey = null,
        nextKey = 2,
        createdAt = Clock.System.now().toEpochMilliseconds(),  // Fresh!
        orderIndex = 0
    )
)
```

### 2. Not Using Transactions
**Problem:** Partial updates leave inconsistent state

**Solution:** Always wrap multi-step DB operations:
```kotlin
transactionProvider.runAsTransaction {
    localDataSource.clearRemoteKeys()
    localDataSource.upsertArticles(articles)
    localDataSource.upsertRemoteKeys(keys)
}
```

### 3. Blocking UI with checkCacheExpiry
**Problem:** Calling `refresh()` synchronously blocks Flow emission

**Solution:** Use `CoroutineScope` to launch in background:
```kotlin
val scope = CoroutineScope(Dispatchers.Default)
scope.launch {
    checkCacheExpiry()
}
```

### 4. Not Handling End of Pagination
**Problem:** Infinite API calls when `nextKeyId` is null

**Solution:** Check for null and return early:
```kotlin
val nextKey = lastRemoteKey?.nextKey
if (nextKey == null) {
    return Result.Success(Unit)  // End reached
}
```

---

## Future Enhancements

### Bidirectional Pagination
- Use `prevKey` for "load previous" functionality
- Useful for chat apps or timelines

### Dynamic Page Size
- Adjust page size based on network speed
- Larger batches on WiFi, smaller on cellular

### Prefetching
- Load next page when user is 80% through current page
- Smoother scrolling experience

### Cache Invalidation Strategies
- Time-based (current: 1 hour)
- Event-based (e.g., on app foreground)
- Manual (user-triggered)

---

## References

- [News Feed Implementation](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/repository/NewsFeedRepositoryImpl.kt)
- [Data Layer Architecture](file:///Users/frenskylee/Documents/git/kmpBoiler/.agent/rules/project_architecture.md)
- [Data Implementation Skill](file:///Users/frenskylee/Documents/git/kmpBoiler/.agent/skills/data_implementation/SKILL.md)
