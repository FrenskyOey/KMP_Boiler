package di


import feature.news.ui.main.NewsFeedViewModel
import feature.settings.ui.main.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    factoryOf(::NewsFeedViewModel)
    factoryOf(::SettingsViewModel)
}
