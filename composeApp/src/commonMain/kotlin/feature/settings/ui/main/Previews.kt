package feature.settings.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import feature.news.domain.model.Article
import feature.news.ui.main.composable.NewsEmptyWidget
import feature.news.ui.main.composable.NewsErrorWidget
import feature.news.ui.main.composable.NewsItemWidget
import feature.settings.ui.main.SettingScreen

// Preview for SettingScreen with sample data
@Preview
@Composable
fun PreviewSettingScreen() {
    SettingScreen()
}
