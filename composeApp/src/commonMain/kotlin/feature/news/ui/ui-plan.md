# UI Layer Implementation Plan - News Detail

## Goal
Implement the UI for the News Detail screen, visualizing the `NewsDetail` domain model and handling user interactions (share, back, retry).

## User Review Required
> [!IMPORTANT]
> - **Navigation**: Takes `articleId` (Int) AND `articleTitle` (String) as navigation arguments.
> - **DI**: Needs to register `NewsDetailViewModel` in `NewsModule.kt`.
> - **Content Rendering**: Supports `Paragraph` and `Quote` types from `NewsContent` sealed interface.

## Proposed Changes

### feature/news/ui

#### [NEW] [NewsDetailState.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/detail/NewsDetailState.kt)
- **NewsDetailState**:
    - `articleId: Int`
    - `articleTitle: String` (Initial title from nav arg)
    - `isLoading: Boolean`
    - `article: NewsDetail?`
    - `error: String?`
- **NewsDetailEvent**:
    - `LoadDetail(id: Int, title: String)`
    - `OnShareClick(url: String)`
    - `OnBackClick`
    - `OnRetry`
- **NewsDetailEffect**:
    - `ShowSnackbar(message: String)`
    - `NavigateBack`

#### [NEW] [NewsDetailViewModel.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/detail/NewsDetailViewModel.kt)
- **Dependencies**: `GetNewsDetailUseCase`, `RefreshNewsDetailUseCase`.
- **Logic**:
    - `init`: Trigger `refreshNewsDetail(id)` to fetch latest data.
    - `Observation`: Collect `getNewsDetail(id)` flow to update `state.article`.
    - `Error Handling`: Catch errors from refresh and show via state/effect.
    - `Sharing`: Use `ClipboardManager` (platform specific or expect/actual helper) or simple intent sharing if available. For now, plan to copy to clipboard or just log.

#### [NEW] [NewsArticleContent.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/detail/components/NewsArticleContent.kt)
- **Composables**:
    - `ArticleParagraph(text: String)`
    - `ArticleQuote(text: String, highlighted: Boolean, emphasis: Boolean)`
    - `NewsArticleContent(content: List<NewsContent>)`: Iterates and delegates to above composables.

#### [NEW] [NewsDetailScreen.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/detail/NewsDetailScreen.kt)
- **Structure**:
    - `Scaffold` with `TopAppBar`.
    - `LazyColumn` for content:
        - Header Image (`article.image`)
        - Title (`article.title`)
        - Metadata Row (`category`, `publishedAt`, `readTime`)
        - Author Info (`author.name`, `author.avatar`)
        - Content (`NewsArticleContent`)
        - Tags (`article.tags`)

#### [MODIFY] [NewsModule.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/di/NewsModule.kt)
- Register `NewsDetailViewModel` using `factoryOf(::NewsDetailViewModel)`.

## Verification Plan

### Automated Tests
- **ViewModel Tests**: Verify `LoadDetail` triggers use cases and updates state. Verify `OnRetry` logic.
    - Run: `./gradlew testDebugUnitTest` (or specific test command)

### Manual Verification
1. **Navigation**: Click a news item from Feed -> Opens Detail.
2. **Content Display**: Verify Title, Image, and Paragraphs render correctly.
3. **States**:
    - **Loading**: Show progress indicator while fetching.
    - **Error**: Disconnect network, open detail -> Show Error + Retry.
    - **Success**: Verify data matches the `NewsDetail` object.
4. **Interactions**:
    - Click Back -> Returns to Feed.
    - Click Share -> Copies URL (verify toast/snackbar).
