# UI Layer Plan - News Feed

## Overview
- **Screen**: List of News Articles.
- **Architecture**: MVI (Model-View-Intent).
- **Interactions**: Infinite Scroll, Pull-to-Refresh, Retry Error.

## 1. State Management
### 1.1 UI State
```kotlin
data class NewsState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val page: Int = 1,
    val isEndReached: Boolean = false,
    val totalCachedCount: Int = 0
)
```

### 1.2 UI Effect
```kotlin
sealed interface NewsEffect {
    data class ShowToast(val message: String) : NewsEffect
    data class ShowError(val message: String) : NewsEffect
}
```

### 1.3 UI Intent
```kotlin
sealed interface NewsIntent {
    data object LoadNextPage : NewsIntent
    data object Refresh : NewsIntent
    data object Retry : NewsIntent
}
```

## 2. ViewModel
### 2.1 Dependencies
- `GetNewsFeedUseCase` (or Repository directly, but UseCase preferred per Clean Arch rules in `domain-plan.md`).

### 2.2 Logic
- **Init**: Load Page 1. Observe Article Count.
- **LoadNextPage**: Increment page, fetch, append to list. Handle empty list = EndReached.
- **Refresh**: Clear list (or keep and replace?), Page = 1, Fetch.
- **Retry**: Re-fetch current page.
- **Effects**: Send `ShowToast` on success (with new item count). Send `ShowError` on pagination failure.

## 3. Compose Components
### 3.1 Components
- `ArticleItem`: Card with Image, Title, Topic, Snippet.
- `NewsScreen`: Scaffold, TopBar, LazyColumn.

### 3.2 Error Handling
- **Full Screen Error**: If list is empty and error occurs.
- **Toast**: If list has data and error occurs.

## 4. Navigation
- Route: `NewsFeed` (or similar).
- Entry point in `App.kt` (or NavGraph).

## 5. Dependency Injection
- `newsModule` (Shared):
    - `factory { GetNewsFeedUseCase(get()) }`
    - `single { NewsFeedRepositoryImpl(...) }`
- `appModule` (Android/Shared UI):
    - `factory { NewsViewModel(get()) }` (Use factory for MVI/ViewModel)

## 6. Implementation Checklist
> **Note**: This plan is dynamic. During implementation (Phase 4), if design analysis reveals new requirements (e.g., tabs, settings), I will pause, update this plan, and request approval before proceeding.

- [ ] **Verify Design Reference**: Ask user for UI mockups/images. Analyze if provided.
- [ ] Define `NewsState`, `NewsEffect`, `NewsIntent`
- [ ] Implement `NewsViewModel` with MVI
- [ ] Create `ArticleItem` & `NewsScreen`
- [ ] Update `App.kt` / Navigation
- [ ] Setup DI modules
- [ ] Manual Verification
