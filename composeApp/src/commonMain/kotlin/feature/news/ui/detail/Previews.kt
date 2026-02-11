package feature.news.ui.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import core.components.CoreAvatar
import feature.news.domain.model.NewsContent
import feature.news.domain.model.NewsDetail
import feature.news.ui.detail.components.NewsAuthor
import feature.news.ui.detail.components.NewsDetailContent
import feature.news.ui.detail.components.NewsDetailHeader
import feature.news.ui.detail.components.NewsDetailTags
import feature.news.ui.detail.state.NewsDetailState
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun NewsDetailScreenContentPreview() {
    val mockArticle = NewsDetail(
        id = 4,
        title = "Revolutionary Gene Therapy Shows Promise Against Alzheimer's",
        category = "HEALTH",
        image = "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800",
        author = NewsDetail.Author("Dr. Robert Kim", "https://i.pravatar.cc/150?img=33", "Medical Journal Today"),
        publishedAt = "2026-02-07T11:20:00Z", // User sample
        readTime = 8,
        content = listOf(
            NewsContent.Paragraph("This is the first paragraph of the article. It contains some text to demonstrate how the typography looks."),
            NewsContent.Quote("This is a pull quote to highlight important information."),
            NewsContent.Paragraph("Another paragraph follows the quote.")
        ),
        tags = listOf("Genetics", "Science", "Medicine"),
        shareUrl = "https://educationtomorrow.edu/articles/ai-tutors-personalized-learning-breakthrough"
    )
    
    NewsDetailScreenContent(
        state = NewsDetailState(
            articleId = 1,
            articleTitle = "Preview Title",
            isLoading = false,
            article = mockArticle
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}

@Preview
@Composable
private fun NewsDetailLoadingPreview() {
    NewsDetailScreenContent(
        state = NewsDetailState(
            isLoading = true,
            article = null
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}

@Preview
@Composable
private fun NewsDetailErrorPreview() {
    NewsDetailScreenContent(
        state = NewsDetailState(
            error = "Network Error: Failed to load article.",
            isLoading = false,
            article = null
        ),
        snackbarHostState = remember { SnackbarHostState() },
        onEvent = {},
        modifier = Modifier.fillMaxSize()
    )
}

@Preview
@Composable
private fun CoreAvatarPreview() {
    CoreAvatar(
        imageUrl = null,
        name = "Jane Doe"
    )
}

@Preview
@Composable
private fun NewsDetailContentPreview() {
    NewsDetailContent(
        content = listOf(
            NewsContent.Paragraph("Paragraph 1 text."),
            NewsContent.Quote("Quote text."),
            NewsContent.Paragraph("Paragraph 2 text.")
        )
    )
}

@Preview
@Composable
private fun NewsDetailTagsPreview() {
    NewsDetailTags(
        tags = listOf("Technology", "Compose", "Kotlin", "Mobile Development")
    )
}

@Preview
@Composable
private fun NewsDetailHeaderPreview() {
    NewsDetailHeader(
        imageUrl = "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800",
        category = "HEALTH",
        title = "Revolutionary Gene Therapy Shows Promise Against Alzheimer's"
    )
}

@Preview
@Composable
private fun NewsAuthorPreview() {
    NewsAuthor(
        author = NewsDetail.Author("Dr. Robert Kim", "https://i.pravatar.cc/150?img=33", "Medical Journal Today"),
        publishedAt = "2026-02-07T11:20:00Z",
        readTime = 8
    )
}
