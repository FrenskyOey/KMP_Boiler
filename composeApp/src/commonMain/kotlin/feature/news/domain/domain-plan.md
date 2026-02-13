# Domain Layer Implementation Plan - News Feed Refactor

## Goal
Update the Domain Layer to support `key_id` based pagination and Room-based Single Source of Truth (SSOT). The repository will now expose a `Flow` of articles instead of a suspend function returning a list.

## User Review Required
> [!IMPORTANT]
> The `NewsFeedRepository` interface is changing significantly.
> - **Old**: `suspend fun getArticles(page: Int): Result<List<Article>>`
> - **New**: 
>   - `fun getArticles(): Flow<List<Article>>` (Observes Local DB)
>   - `suspend fun refresh(): Result<Unit>` (Force API fetch & DB reset)
>   - `suspend fun loadNextPage(): Result<Unit>` (Fetch next page & append)

## Proposed Changes

### Clean Up Outdated Tests
#### [DELETE] [GetNewsFeedUseCaseTest.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonTest/kotlin/feature/news/domain/usecase/newsfeed/GetNewsFeedUseCaseTest.kt)
- The existing test assumes a `suspended List` return. It is invalid for the new `Flow` based approach.
- We will delete this file and create a new TDD test file from scratch.

### Feature: News

#### [MODIFY] [NewsFeedRepository.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/domain/repository/NewsFeedRepository.kt)
- Change `getArticles` to return `Flow<List<Article>>`.
- Add `suspend fun refresh(): Result<Unit>`
- Add `suspend fun loadNextPage(): Result<Unit>`
- Keep `getArticleCount(): Flow<Int>` (if still needed, or remove if redundant).

#### [MODIFY] [GetNewsFeedUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/domain/usecase/newsfeed/GetNewsFeedUseCase.kt)
- Update to call `repository.getArticles()`.
- Return `Flow<List<Article>>`.

#### [NEW] [RefeshNewsFeedUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/domain/usecase/newsfeed/RefeshNewsFeedUseCase.kt)
- Encapsulate `repository.refresh()`.

#### [NEW] [LoadMoreNewsUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/domain/usecase/newsfeed/LoadMoreNewsUseCase.kt)
- Encapsulate `repository.loadNextPage()`.

## Verification Plan

### Automated Tests
- **UnitTest**: `GetNewsFeedUseCaseTest` (Re-created)
  - Verify it emits values from repository flow.
- **UnitTest**: `NewsFeedRepositoryTest` (in Data phase, but interface defined here).

### Manual Verification
- None for Domain layer specifically (verified via Data layer integration).
