package feature.news.di

import core.data.local.database.AppDatabase
import feature.news.data.api.NewsApiService
import feature.news.data.api.NewsApiServiceImp
import feature.news.data.repository.NewsFeedRepositoryImpl
import feature.news.domain.repository.NewsFeedRepository
import feature.news.domain.usecase.newsfeed.CheckCacheExpiredUseCase
import feature.news.domain.usecase.newsfeed.GetNewsFeedUseCase
import feature.news.domain.usecase.newsfeed.LoadMoreNewsUseCase
import feature.news.domain.usecase.newsfeed.RefreshNewsFeedUseCase
import feature.news.domain.usecase.newsdetail.RefreshNewsDetailUseCase
import feature.news.domain.usecase.newsdetail.GetNewsDetailUseCase
import feature.news.ui.detail.NewsDetailViewModel
import feature.news.ui.main.NewsFeedViewModel
import feature.settings.ui.main.SettingsViewModel
import feature.news.data.api.NewsDetailApiService
import feature.news.data.api.NewsDetailApiServiceImpl
import feature.news.data.datasource.NewsDetailDataSource
import feature.news.data.datasource.local.NewsDetailLocalDataSourceImpl
import feature.news.data.datasource.remote.NewsDetailRemoteDataSourceImpl
import feature.news.data.repository.NewsDetailRepositoryImpl
import feature.news.domain.repository.NewsDetailRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val newsModule = module {
    // API
    single<NewsApiService>{ NewsApiServiceImp(get(), get()) }
    // Local
    single { get<AppDatabase>().newsDao() }
    single { get<AppDatabase>().newsRemoteKeysDao() }

    // Data Sources
    single<feature.news.data.datasource.NewsDataSource.Remote> { feature.news.data.datasource.remote.NewsRemoteDataSourceImpl(get()) }
    single<feature.news.data.datasource.NewsDataSource.Local> { feature.news.data.datasource.local.NewsLocalDataSourceImpl(get(), get()) }

    // Repository
    single<NewsFeedRepository> { 
        NewsFeedRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get()
        )
    }
    
    // UseCases
    factoryOf(::GetNewsFeedUseCase)
    factoryOf(::RefreshNewsFeedUseCase)
    factoryOf(::LoadMoreNewsUseCase)
    factoryOf(::CheckCacheExpiredUseCase)

    factory { NewsFeedViewModel(get(), get(), get(), get()) }
    factoryOf(::SettingsViewModel)

    // --- News Detail ---
    // API
    single<NewsDetailApiService> { NewsDetailApiServiceImpl(get(), get()) }
    
    // Data Sources
    single<NewsDetailDataSource.Remote> { NewsDetailRemoteDataSourceImpl(get()) }
    single<NewsDetailDataSource.Local> { NewsDetailLocalDataSourceImpl(get()) }
    
    // DAO
    single { get<AppDatabase>().newsDetailDao() }
    
    // Repository
    single<NewsDetailRepository> { 
        NewsDetailRepositoryImpl(
            remoteDataSource = get(),
            localDataSource = get()
        )
    }
    
    // News Detail UseCases
    factory { GetNewsDetailUseCase(get()) }
    factory { RefreshNewsDetailUseCase(get()) }
    
    // ViewModels
    viewModelOf(::NewsDetailViewModel)
}
