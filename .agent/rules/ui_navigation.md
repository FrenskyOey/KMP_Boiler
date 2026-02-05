---
description: UI organization and navigation rules
---

# KMP UI Organization

## Feature-Based UI Structure

```kotlin
composeApp/src/commonMain/kotlin/
├── core/
│   ├── theme/                 # Shared theme
│   ├── navigation/            # Navigation setup
│   └── components/            # Shared components
└── feature/
    ├── news/
    │   ├── data/
    │   ├── domain/
    │   ├── di/
    │   └── ui/
    │       ├── main/
    │       │   ├── NewsScreen.kt
    │       │   ├── NewsFeedViewModel.kt
    │       │   └── components/
    │       └── detail/
    │           ├── NewsDetailScreen.kt
    │           └── NewsDetailViewModel.kt
    └── settings/
        ├── data/
        ├── domain/
        ├── di/
        └── ui/
            └── main/
                ├── SettingScreen.kt
                ├── SettingsViewModel.kt
                └── components/
```

## UI Rules

```kotlin
// ✅ CORRECT - ViewModel uses use cases from same feature
class NewsFeedViewModel(
    private val getNewsFeedUseCase: GetNewsFeedUseCase,
    private val refreshNewsFeedUseCase: RefreshNewsFeedUseCase
) : ViewModel()

// ❌ WRONG - ViewModel uses use cases from different feature
class NewsFeedViewModel(
    private val getNewsFeedUseCase: GetNewsFeedUseCase,
    private val getSettingsUseCase: GetSettingsUseCase  // WRONG!
) : ViewModel()
```

## Navigation Between Features

```kotlin
// ✅ CORRECT - Type-Safe Navigation at app level
@Serializable object NewsFeedList
@Serializable data class NewsDetail(val id: String)
@Serializable object Settings

NavHost(navController, startDestination = NewsFeedList) {
    // News feature screens
    composable<NewsFeedList> {
        NewsScreen(
            onNewsItemClick = { id -> navController.navigate(NewsDetail(id)) },
            onSettingsClick = { navController.navigate(Settings) }
        )
    }
    
    composable<NewsDetail> { backStackEntry ->
        val args = backStackEntry.toRoute<NewsDetail>()
        NewsDetailScreen(
            newsId = args.id,
            onBackClick = { navController.popBackStack() }
        )
    }
    
    // Settings feature screens
    composable<Settings> {
        SettingScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
```
