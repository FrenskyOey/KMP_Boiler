package feature.news.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import feature.news.domain.model.Article

/*
// Preview for ArticleItem with sample data
@Preview(name = "Article Item - Light", showBackground = true)
@Composable
fun PreviewArticleItem() {
    ArticleItem(
        article = Article(
            id = 1,
            title = "Breaking: New Kotlin Multiplatform Features Announced",
            content = "Google has announced exciting new features for Kotlin Multiplatform development, including improved iOS support and better tooling integration.",
            imageUrl = "https://picsum.photos/800/450",
            topic = "Technology"
        ),
        onClick = {}
    )
}

@Preview(name = "Article Item - Long Title", showBackground = true)
@Composable
fun PreviewArticleItemLongTitle() {
    ArticleItem(
        article = Article(
            id = 2,
            title = "This is a very long article title that should be truncated after two lines to demonstrate the text overflow behavior in the UI component",
            content = "This is a sample article content that demonstrates how the article preview looks with a very long title. The content should also be truncated after three lines.",
            imageUrl = "https://picsum.photos/800/450",
            topic = "Science",
        ),
        onClick = {}
    )
}
*/

// Preview for EmptyState
@Preview(name = "Empty State - Default", showBackground = true)
@Composable
fun PreviewEmptyState() {
    EmptyState()
}

@Preview(name = "Empty State - Custom Message", showBackground = true)
@Composable
fun PreviewEmptyStateCustom() {
    EmptyState(
        message = "No articles available",
        subMessage = "Try refreshing or check back later for new content."
    )
}

// Preview for ErrorState
@Preview(name = "Error State - Network Error", showBackground = true)
@Composable
fun PreviewErrorState() {
    ErrorState(
        message = "Unable to connect to the server. Please check your internet connection.",
        onRetry = {}
    )
}

@Preview(name = "Error State - Generic Error", showBackground = true)
@Composable
fun PreviewErrorStateGeneric() {
    ErrorState(
        message = "An unexpected error occurred. Please try again.",
        onRetry = {}
    )
}

// Preview for UnderMaintenanceScreen
@Preview(name = "Under Maintenance", showBackground = true)
@Composable
fun PreviewUnderMaintenanceScreen() {
    UnderMaintenanceScreen()
}