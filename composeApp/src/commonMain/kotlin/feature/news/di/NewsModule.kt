package feature.news.di

import feature.news.data.api.NewsApiService
import feature.news.data.datasource.NewsDataSource
import feature.news.data.datasource.local.NewsLocalDataSourceImpl
import feature.news.data.datasource.remote.NewsRemoteDataSourceImpl
import feature.news.data.repository.NewsFeedRepositoryImpl
import feature.news.domain.repository.NewsFeedRepository
import feature.news.domain.usecase.GetNewsFeedUseCase
import org.koin.dsl.module

val newsModule = module {
    // API
    single { NewsApiService(get(), get()) }
    single<NewsDataSource.Remote> { NewsRemoteDataSourceImpl(get()) }

    // Local
    single { get<core.data.local.database.AppDatabase>().newsDao() }
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
    factory { feature.news.domain.usecase.GetArticleCountUseCase(get()) }
}
