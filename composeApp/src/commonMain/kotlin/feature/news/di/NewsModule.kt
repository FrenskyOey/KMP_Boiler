package feature.news.di

import core.data.local.database.AppDatabase
import feature.news.data.api.NewsApiService
import feature.news.data.api.NewsApiServiceImp
import feature.news.data.datasource.NewsDataSource
import feature.news.data.datasource.local.NewsLocalDataSourceImpl
import feature.news.data.datasource.remote.NewsRemoteDataSourceImpl
import feature.news.data.repository.NewsFeedRepositoryImpl
import feature.news.domain.repository.NewsFeedRepository
import feature.news.domain.usecase.newsfeed.GetArticleCountUseCase
import feature.news.domain.usecase.newsfeed.GetNewsFeedUseCase
import feature.news.ui.main.NewsFeedViewModel
import feature.settings.ui.main.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val newsModule = module {
    // API
    single<NewsApiService>{ NewsApiServiceImp(get(), get()) }
    single<NewsDataSource.Remote> { NewsRemoteDataSourceImpl(get()) }

    // Local
    single { get<AppDatabase>().newsDao() }
    single<NewsDataSource.Local> { NewsLocalDataSourceImpl(get()) }

    // Repository
    single<NewsFeedRepository> { 
        NewsFeedRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get()
        )
    }
    
    // Use Cases
    factory { GetNewsFeedUseCase(get()) }
    factory { GetArticleCountUseCase(get()) }

    factoryOf(::NewsFeedViewModel)
    factoryOf(::SettingsViewModel)
}
