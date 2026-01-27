package di

import feature.news.list.NewsFeedViewModel
import feature.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    viewModel { NewsFeedViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
}
