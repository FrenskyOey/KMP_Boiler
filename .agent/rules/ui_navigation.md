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
// ✅ CORRECT - Navigation at app level
NavHost(navController, startDestination = Screen.NewsFeedList.route) {
    // News feature screens
    composable(Screen.NewsFeedList.route) {
        NewsScreen(
            onNewsItemClick = { id -> navController.navigate(Screen.NewsDetail.createRoute(id)) },
            onSettingsClick = { navController.navigate(Screen.Settings.route) }
        )
    }
    
    // Settings feature screens
    composable(Screen.Settings.route) {
        SettingScreen(
            onBackClick = { navController.popBackStack() }
        )
    }
}
```
