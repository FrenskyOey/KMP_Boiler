package com.interview.prep.kmp_learn

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import core.theme.AppTheme

import feature.news.list.NewsScreen

@Composable
fun App() {
    AppTheme {
        NewsScreen()
    }
}
