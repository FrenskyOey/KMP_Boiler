---
description: Naming conventions, file rules, and error handling
---

# Coding Standards & Conventions

## Naming Conventions

**Features:**
- Package: `feature.[feature_name]` (e.g., `feature.news`, `feature.settings`)
- Lowercase, singular preferred

**Classes:**
- PascalCase: `NewsFeedRepository`
- Feature prefix optional but consistent

**Files:**
- Use cases: `[Action][Entity]UseCase.kt`
- Repositories: `[Entity]Repository.kt` (interface), `[Entity]RepositoryImpl.kt` (impl)
- DTOs: `[Entity]Dto.kt`
- Entities: `[Entity]Entity.kt`
- Modules: `[Feature]Module.kt`

## File Location Rules

```kotlin
// ✅ CORRECT - Shared utilities in core
core/data/remote/util/ApiErrorHandler.kt
core/domain/model/Result.kt
core/domain/model/AppException.kt

// ✅ CORRECT - Feature-specific in feature
feature/news/data/remote/api/NewsApiService.kt
feature/news/domain/model/NewsFeed.kt
feature/news/di/NewsModule.kt

// ❌ WRONG - Feature-specific in core
core/domain/model/NewsFeed.kt  // Should be in feature/news/

// ❌ WRONG - Shared utility in feature
feature/news/data/util/ApiErrorHandler.kt  // Should be in core/
```

## Error Handling Rules

### Use Core Error Types

```kotlin
// ✅ CORRECT - Feature uses core error types
suspend fun getNewsFeed(): Result<List<NewsFeed>> {
    return try {
        val data = apiService.getNewsFeed()
        Result.Success(data)
    } catch (e: Exception) {
        Result.Error(ApiErrorHandler.handleError(e))  // From core
    }
}

// ❌ WRONG - Feature creates own error types
sealed class NewsException : Exception()  // Should use core AppException
```
