---
description: Dependency injection setup and rules using Koin
---

# Dependency Injection Rules

## Feature Modules

```kotlin
// ✅ CORRECT - One module per feature
val newsModule = module {
    // API
    single<NewsApiService> { NewsApiServiceImpl(get(), get(), get()) }
    
    // DAO
    single { get<AppDatabase>().newsFeedDao() }
    
    // Repository
    single<NewsFeedRepository> { NewsFeedRepositoryImpl(get(), get(), get()) }
    
    // Use Cases
    factory { GetNewsFeedUseCase(get()) }
    factory { GetNewsDetailUseCase(get()) }
}

// ❌ WRONG - Multiple features in one module
val sharedModule = module {
    single<NewsApiService> { NewsApiServiceImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
}
```

## Module Organization

```kotlin
// Application initialization
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            modules(
                // Core modules first
                coreNetworkModule,
                coreDatabaseModule,
                corePreferencesModule,
                
                // Feature modules
                newsModule,
                settingsModule,
                
                // App module last
                appModule
            )
        }
    }
}
```

## iOS Koin Initialization

**CRITICAL:** iOS apps must call `initKoin()` before any UI is created.

```kotlin
// ✅ CORRECT - KoinHelper.kt (composeApp/iosMain)
fun initKoin() {
    startKoin {
        modules(
            coreConfigModule,      // Include config module
            coreNetworkModule,
            // ... other modules
        )
    }
}

// iOSApp.swift
import ComposeApp

@main
struct iOSApp: App {
    init() {
        KoinHelperKt.initKoin()  // MUST call before UI
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```
