# UI Layer Implementation Plan - News Detail

## Goal
Implement the UI for the News Detail screen, including rich content rendering and state management.

## User Review Required
> [!IMPORTANT]
> - **Navigation**: Takes `articleId` as a navigation argument.
> - **Content Rendering**: Custom Composables for `Paragraph` and `Quote` types.
> - **Share**: Uses `ClipboardManager` to copy `shareUrl`.

## Proposed Changes

### feature/news/ui

#### [NEW] [NewsDetailState.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/detail/NewsDetailState.kt)
- `NewsDetailState`: `isLoading`, `article: NewsDetail?`, `error: String?`
- `NewsDetailEvent`: `LoadDetail(id)`, `OnShareClick`, `OnBackClick`, `OnRetry`
- `NewsDetailEffect`: `ShowSnackbar`, `NavigateBack`

#### [NEW] [NewsDetailViewModel.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/detail/NewsDetailViewModel.kt)
- Loads data on init (using `savedStateHandle` or passed ID).
- Handles `OnShareClick` -> Copy to clipboard.

#### [NEW] [NewsDetailScreen.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/detail/NewsDetailScreen.kt)
- **Scaffold**: TopBar with Back and Share buttons.
- **Content**: Scrollable Column.
  - Image Header
  - Title & Author Info
  - Content Block (Iterate list and render appropriate composable)
- **States**: 
  - Loading: CircularProgressIndicator (Centered)
  - Error: Text + Retry Button
  - Success: The Article Content

#### [NEW] [NewsArticleContent.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/ui/detail/components/NewsArticleContent.kt)
- `ArticleParagraphComponent`: Text with styling.
- `ArticleQuoteComponent`: Styled quote (Italic/Border).

## Verification
- **Manual**: Verify `xid=0` to `7` load different mocked data.
- **Manual**: Verify offline mode (cache invalidation).
- **Manual**: Verify Share button copies correct URL.
