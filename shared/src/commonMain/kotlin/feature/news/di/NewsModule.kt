package feature.news.di

import feature.news.data.repository.NewsFeedRepositoryImpl
import feature.news.domain.repository.NewsFeedRepository
import feature.news.domain.usecase.GetNewsFeedUseCase
import org.koin.dsl.module

val newsModule = module {
    // Repository
    single<NewsFeedRepository> { 
        NewsFeedRepositoryImpl()
    }
    
    // Use Cases
    factory { GetNewsFeedUseCase(get()) }
}
