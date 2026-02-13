# Data Layer Implementation Plan - News Feed Refactor

## Goal
Implement Room-based caching with `key_id` pagination. The Data Layer will manage the logic of fetching from API, storing in DB, and emitting updates via Flow.

## Proposed Changes

### Clean Up Outdated Tests
#### [DELETE] [NewsFeedRepositoryImplTest.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonTest/kotlin/feature/news/data/repository/NewsFeedRepositoryImplTest.kt)
- The existing test assumes direct API-to-Domain mapping.
- We will delete this and implement a new integration test for the CQS pattern (DB observation + API fetch).

### Database (Room)
#### [NEW] [NewsRemoteKeysEntity.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/model/entity/NewsRemoteKeysEntity.kt)
- Fields: `articleId` (PK), `prevKey`, `nextKey`, `createdAt`.

#### [MODIFY] [ArticleEntity.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/model/entity/ArticleEntity.kt)
- Ensure compatibility with new API response fields if any.

#### [NEW] [NewsRemoteKeysDao.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/dao/NewsRemoteKeysDao.kt)
- `insertAll(remoteKeys: List<NewsRemoteKeysEntity>)`
- `getRemoteKeys(articleId: String): NewsRemoteKeysEntity?`
- `clearRemoteKeys()`

#### [MODIFY] [NewsDao.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/dao/NewsDao.kt)
- `getAllArticles(): Flow<List<ArticleEntity>>`
- `insertAll(articles: List<ArticleEntity>)`
- `clearAll()`

#### [MODIFY] [AppDatabase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/core/data/local/database/AppDatabase.kt)
- Add `NewsRemoteKeysEntity` to entities.
- Add `abstract fun newsRemoteKeysDao(): NewsRemoteKeysDao`.

### API
#### [MODIFY] [NewsApiService.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/api/NewsApiService.kt)
- Update `fetchArticles` to accept `keyId: Int` (or String) instead of `page`.
- Return response with `pagination` object containing `next_key`.

#### [MODIFY] [ArticleListResponse.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/model/response/ArticleListResponse.kt)
- Update structure to match new JSON (include `pagination` field).

### Repository
#### [MODIFY] [NewsFeedRepositoryImpl.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/repository/NewsFeedRepositoryImpl.kt)
- **Dependencies**: `NewsApiService`, `NewsDao`, `NewsRemoteKeysDao`, `AppDatabase` (for transactions).
- **Logic**:
  - `getArticles()`: Returns `newsDao.getAllArticles().map { ... }`.
  - `refresh()`:
    - Check usage timestamp (if needed for smart refresh, otherwise force).
    - Fetch API `key_id=0`.
    - Transaction: Clear tables -> Insert Keys -> Insert Articles.
  - `loadNextPage()`:
    - Get last item from DB.
    - Get corresponding RemoteKey.
    - If `nextKey` is null, return (End of List).
    - Fetch API `nextKey`.
    - Transaction: Insert Keys -> Insert Articles.

### DI
#### [MODIFY] [NewsModule.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/di/NewsModule.kt)
- Provide `NewsRemoteKeysDao`.
- Update `NewsFeedRepositoryImpl` parameters.

## Verification Plan

### Automated Tests
- **IntegrationTest**: `NewsFeedRepositoryImplTest`
  - Mock API and DB.
  - Test `refresh()` clears DB and inserts new data.
  - Test `loadNextPage()` appends data.
  - Test `getArticles()` emits updates.

### Manual Verification
- **Offline Mode**: Turn off network, open app. Should see cached data.
- **Refresh**: Pull to refresh should update data.
- **Pagination**: Scroll to bottom, should load more items.
