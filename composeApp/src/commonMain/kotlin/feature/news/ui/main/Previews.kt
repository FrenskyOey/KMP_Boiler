package feature.news.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import feature.news.domain.model.Article
import feature.news.ui.main.components.NewsEmptyWidget
import feature.news.ui.main.components.NewsErrorWidget
import feature.news.ui.main.components.NewsItemWidget
import feature.settings.ui.main.SettingScreen

// Preview for ArticleItem with sample data
@Preview
@Composable
fun PreviewArticleItem() {
    NewsItemWidget(
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

@Preview
@Composable
fun PreviewArticleItemLongTitle() {
    NewsItemWidget(
        article = Article(
            id = 2,
            title = "This is a very long article title that should be truncated after two lines to demonstrate the text overflow behavior in the UI component",
            content = "This is a sample article content that demonstrates how the article preview looks with a very long title. The content should also be truncated after three lines.",
            imageUrl = "https://picsum.photos/800/450",
            topic = "Science"
        ),
        onClick = {}
    )
}

// Preview for EmptyState
@Preview
@Composable
fun PreviewEmptyState() {
    NewsEmptyWidget()
}

// Preview for ErrorState
@Preview
@Composable
fun PreviewErrorState() {
    NewsErrorWidget(
        message = "Unable to connect to the server. Please check your internet connection.",
        onRetry = {}
    )
}

// Preview for UnderMaintenanceScreen
@Preview
@Composable
fun PreviewUnderMaintenanceScreen() {
    SettingScreen()
}
