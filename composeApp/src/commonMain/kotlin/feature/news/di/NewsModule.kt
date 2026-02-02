package feature.news.di

import feature.news.data.repository.NewsFeedRepositoryImpl
import feature.news.domain.repository.NewsFeedRepository
import feature.news.domain.usecase.GetNewsFeedUseCase
import org.koin.dsl.module

val newsModule = module {
    // API
    single { feature.news.data.remote.NewsApiService(get(), get()) }
    single<feature.news.data.remote.NewsRemoteDataSource> { feature.news.data.remote.NewsRemoteDataSourceImpl(get()) }

    // Local
    single { get<core.data.local.database.AppDatabase>().newsDao() }
    single<feature.news.data.local.NewsLocalDataSource> { feature.news.data.local.NewsLocalDataSourceImpl(get()) }

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
