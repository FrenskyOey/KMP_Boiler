# UI Layer Implementation Plan - News Feed Refactor

## Goal
Update `NewsFeedViewModel` to consume the `Flow` from the repository and handle pagination actions ("Load More", "Refresh"). The UI components generally remain the same but need to integrate with the new ViewModel state.

## Proposed Changes

### ViewModel
#### [MODIFY] [NewsFeedViewModel.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/main/NewsFeedViewModel.kt)
- **State**:
  - `articles: StateFlow<List<Article>>` (or UI state wrapper).
  - `isLoading: StateFlow<Boolean>`
  - `error: StateFlow<String?>`
- **Init**:
  - Launch `getNewsFeedUseCase()` collection.
  - Check last update time. If > 1 hour, trigger `refreshNewsFeedUseCase()`.
- **Actions**:
  - `onLoadMore()`: Call `loadMoreNewsUseCase()`.
  - `onRefresh()`: Call `refreshNewsFeedUseCase()`.

### UI Components
#### [MODIFY] [NewsScreen.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/main/NewsScreen.kt)
- Observe `articles` flow.
- **Infinite Scroll**: Detect end of list (using `LazyListState`) and invoke `viewModel.onLoadMore()`.
- **Pull to Refresh**: Add `PullRefreshIndicator` (or M3 equivalent) triggering `viewModel.onRefresh()`.

#### [MODIFY] [NewsFeedState.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/feature/news/ui/main/state/NewsContract.kt)
- Update state definition to support "Appending Loading" vs "Full Screen Loading" if needed (though usually list + footer loader is enough).

## Verification Plan

### Automated Tests
- **UnitTest**: `NewsFeedViewModelTest`
  - Verify `init` triggers refresh if stale.
  - Verify `onLoadMore` calls use case.
  - Verify state updates from Flow emission.

### Manual Verification
- **Pagination**: Scroll to bottom -> Verify more items load.
- **Refresh**: Pull down -> Verify refresh occurs.
- **Stale Check**: Modify system time/logic -> Verify auto-refresh on launch.
