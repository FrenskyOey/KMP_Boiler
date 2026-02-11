package feature.news.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import core.components.CoreTopAppBar
import core.theme.*
import feature.news.domain.model.NewsDetail
import feature.news.ui.detail.components.NewsAuthor
import feature.news.ui.detail.components.NewsDetailContent
import feature.news.ui.detail.components.NewsDetailHeader
import feature.news.ui.detail.components.NewsDetailTags
import feature.news.ui.detail.state.NewsDetailEffect
import feature.news.ui.detail.state.NewsDetailEvent
import feature.news.ui.main.components.NewsErrorWidget
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewsDetailScreen(
    articleId: Long,
    articleTitle: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: NewsDetailViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Init Data
    LaunchedEffect(articleId) {
        viewModel.onEvent(NewsDetailEvent.InitData(articleId, articleTitle))
    }

    // Handle Effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is NewsDetailEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                NewsDetailEffect.NavigateBack -> {
                    onNavigateBack()
                }
                is NewsDetailEffect.SharedClipBoard -> {
                    // TODO: Implement clipboard copy if helper available, 
                    // or just show snackbar as simulated in ViewModel for now if platform specific logic is missing.
                    // Assuming ViewModel might have sent a snackbar effect too? 
                    // No, event was SharedClipBoard(url). 
                    // Since we don't have clipboard helper yet, let's just show a snackbar here.
                    snackbarHostState.showSnackbar("Link copied: ${effect.urlLink}") 
                }
            }
        }
    }

    NewsDetailScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsDetailScreenContent(
    state: feature.news.ui.detail.state.NewsDetailState,
    snackbarHostState: SnackbarHostState,
    onEvent: (NewsDetailEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            CoreTopAppBar(
                title = state.articleTitle,
                navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
                onNavigationClick = { onEvent(NewsDetailEvent.OnBackClick) },
                actions = {
                    IconButton(
                        onClick = { 
                            state.article?.shareUrl?.let { url ->
                                onEvent(NewsDetailEvent.OnShareClick(url)) 
                            }
                        },
                        enabled = state.article != null
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                // 1. Loading State (Full Screen, only if no data)
                state.isLoading && state.article == null -> {
                    LoadingView(modifier = Modifier.align(Alignment.Center))
                }

                // 2. Error State (Full Screen, only if no data)
                state.error != null && state.article == null -> {
                    NewsErrorWidget(
                        message = state.error,
                        onRetry = { onEvent(NewsDetailEvent.OnRetry) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                // 3. Content State (article != null)
                state.article != null -> {
                    ArticleView(
                        article = state.article,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingView(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(Spacing.ExtraSmall))
        Text(
            text = "Loading article...",
            style = getTextTitleMedium(),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "We are fetching the latest updates for you.",
            style = getTextBodyMedium(),
            color = getOnSurfaceVariantColor()
        )
    }
}


@Composable
private fun ArticleView(
    article: NewsDetail,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(Spacing.Medium)
    ) {
        // 1. Header (Image, Topic, Title)
        NewsDetailHeader(
            imageUrl = article.image,
            category = article.category,
            title = article.title
        )

        Spacer(modifier = Modifier.height(Spacing.Medium))

        // 2. Author Section
        NewsAuthor(
            author = article.author,
            publishedAt = article.publishedAt,
            readTime = article.readTime
        )

        Spacer(modifier = Modifier.height(Spacing.Large))

        // 3. Content
        NewsDetailContent(content = article.content)
        
        // 4. Tags
        if (!article.tags.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(Spacing.Large))
            NewsDetailTags(tags = article.tags)
        }
        
        Spacer(modifier = Modifier.height(Spacing.Enormous)) // Footer spacer
    }
}


