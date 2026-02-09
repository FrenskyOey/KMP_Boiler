import feature.onboarding.data.api.AuthApiService
import feature.onboarding.data.api.AuthApiServiceImpl
import feature.onboarding.data.datasource.AuthDataSource
import feature.onboarding.data.datasource.local.AuthLocalDataSourceImpl
import feature.onboarding.data.datasource.remote.AuthRemoteDataSourceImpl
import feature.onboarding.data.repository.AuthRepositoryImpl
import feature.onboarding.domain.repository.AuthRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val onboardingModule = module {
    // API Service
    single<AuthApiService> { AuthApiServiceImpl(get(), get()) }

    // Data Sources
    single<AuthDataSource.Remote> { AuthRemoteDataSourceImpl(get()) }
    single<AuthDataSource.Local> { AuthLocalDataSourceImpl(get(named("secure"))) }
    
    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    
    // UseCases (will be added later)
}
