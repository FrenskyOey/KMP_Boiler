package di

import feature.news.list.NewsFeedViewModel
import feature.settings.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    factoryOf(::NewsFeedViewModel)
    factoryOf(::SettingsViewModel)
}
