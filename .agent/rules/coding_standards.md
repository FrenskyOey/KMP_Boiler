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

**Data Layer (feature/data):**
- API Interface: `[Feature]ApiService.kt` (e.g., `NewsApiService`)
- DAO Interface: `[Feature]Dao.kt` (e.g., `NewsDao`)
- Data Source Interface: `[Feature][SourceType]DataSource.kt` (e.g., `NewsRemoteDataSource`)
- Data Source Implementation: `[Feature][SourceType]DataSourceImpl.kt` (e.g., `NewsRemoteDataSourceImpl`)

## File Location Rules

```kotlin
// CORRECT - Shared utilities in core
core/data/remote/util/ApiErrorHandler.kt
core/domain/model/Result.kt
core/domain/model/AppException.kt

// CORRECT - Feature-specific in feature
feature/news/data/remote/api/NewsApiService.kt
feature/news/domain/model/NewsFeed.kt
feature/news/di/NewsModule.kt

// WRONG - Feature-specific in core
core/domain/model/NewsFeed.kt  // Should be in feature/news/

// WRONG - Shared utility in feature
feature/news/data/util/ApiErrorHandler.kt  // Should be in core/
```

## Error Handling Rules

### Use Core Error Types

```kotlin
// CORRECT - Feature uses core error types
suspend fun getNewsFeed(): Result<List<NewsFeed>> {
    return try {
        val data = apiService.getNewsFeed()
        Result.Success(data)
    } catch (e: Exception) {
        Result.Error(ApiErrorHandler.handleError(e))  // From core
    }
}

// WRONG - Feature creates own error types
sealed class NewsException : Exception()  // Should use core AppException
```

---

## Code Quality Rules

### Readable and Well-Named Code

```kotlin
// WRONG - Unclear naming
fun proc(d: List<N>): List<N> {
    return d.filter { it.s == 1 }.map { it.copy(s = 2) }
}

// CORRECT - Clear, descriptive naming
fun processActiveNews(newsItems: List<NewsItem>): List<NewsItem> {
    return newsItems
        .filter { it.status == NewsStatus.ACTIVE }
        .map { it.copy(status = NewsStatus.PUBLISHED) }
}
```

### Small Functions (<50 lines)

```kotlin
// WRONG - Large function doing too much
fun loadAndProcessData() {
    // 100+ lines of code handling multiple responsibilities
}

// CORRECT - Single responsibility, small functions
fun loadNewsData(): Result<List<NewsItem>> {
    return repository.getNews()
}

fun filterActiveNews(items: List<NewsItem>): List<NewsItem> {
    return items.filter { it.isActive }
}

fun mapToUiState(items: List<NewsItem>): NewsUiState {
    return NewsUiState(items = items.map { it.toUiModel() })
}
```

### Focused Files (<800 lines)

```kotlin
// WRONG - Monolithic file with multiple concerns
// NewsScreen.kt (1200+ lines with UI, business logic, mappers)

// CORRECT - Split into focused files
// NewsScreen.kt         - UI composables only
// NewsViewModel.kt      - State management
// NewsUiState.kt        - UI state models
// NewsMapper.kt         - Domain to UI mapping
```

### No Deep Nesting (>4 levels)

```kotlin
// WRONG - Deep nesting (5+ levels)
fun processNews(news: List<NewsItem>?) {
    news?.let { items ->
        items.forEach { item ->
            if (item.isValid) {
                item.categories.forEach { category ->
                    if (category.isActive) {
                        // Level 5+ - too deep!
                    }
                }
            }
        }
    }
}

// CORRECT - Early returns and extracted functions
fun processNews(news: List<NewsItem>?) {
    if (news.isNullOrEmpty()) return
    
    news.filter { it.isValid }
        .flatMap { it.categories }
        .filter { it.isActive }
        .forEach { processCategory(it) }
}
```

### Proper Error Handling

```kotlin
// WRONG - Swallowing exceptions
fun fetchData(): Data? {
    return try {
        api.getData()
    } catch (e: Exception) {
        null  // Silent failure, no context
    }
}

// CORRECT - Proper error propagation with context
suspend fun fetchData(): Result<Data> {
    return try {
        val data = api.getData()
        Result.Success(data)
    } catch (e: Exception) {
        Logger.e("Failed to fetch data", e)
        Result.Error(ApiErrorHandler.handleError(e))
    }
}
```

### No Debug Logging in Production

```kotlin
// WRONG - Debug logs left in code
fun processPayment(amount: Double) {
    println("Processing payment: $amount")  // WRONG
    Log.d("Payment", "Amount: $amount")     // WRONG in production code
}

// CORRECT - Use proper logging with levels
fun processPayment(amount: Double) {
    Logger.d { "Processing payment" }  // Conditionally stripped in release
}

// Or remove entirely for sensitive operations
fun processPayment(amount: Double) {
    // No logging for sensitive financial data
    paymentProcessor.process(amount)
}
```

### No Hardcoded Values

```kotlin
// WRONG - Magic numbers and hardcoded strings
fun fetchNews() {
    val response = api.getNews(limit = 20, timeout = 30000)
    if (response.code == 200) { ... }
}

// CORRECT - Named constants and configuration
object NewsConfig {
    const val DEFAULT_PAGE_SIZE = 20
    const val API_TIMEOUT_MS = 30_000L
}

object HttpStatus {
    const val OK = 200
}

fun fetchNews() {
    val response = api.getNews(
        limit = NewsConfig.DEFAULT_PAGE_SIZE,
        timeout = NewsConfig.API_TIMEOUT_MS
    )
    if (response.code == HttpStatus.OK) { ... }
}
```

### Immutable Patterns (No Mutation)

```kotlin
// WRONG - Mutable state
class NewsViewModel {
    private val _items = mutableListOf<NewsItem>()
    
    fun addItem(item: NewsItem) {
        _items.add(item)  // Mutation!
    }
    
    fun clearItems() {
        _items.clear()  // Mutation!
    }
}

// CORRECT - Immutable state with copy
class NewsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()
    
    fun addItem(item: NewsItem) {
        _uiState.update { currentState ->
            currentState.copy(items = currentState.items + item)
        }
    }
    
    fun clearItems() {
        _uiState.update { currentState ->
            currentState.copy(items = emptyList())
        }
    }
}

// CORRECT - Immutable data transformations
fun processItems(items: List<Item>): List<Item> {
    return items
        .filter { it.isValid }
        .map { it.copy(processed = true) }  // Creates new instances
}
```

---

# Code Quality Checklist

Before marking work complete:

### Readability:
- [ ] Code is readable and well-named (no single-letter variables except loops)
- [ ] Functions have clear, descriptive names indicating their purpose
- [ ] Variables describe what they hold, not their type

### Size Limits:
- [ ] Functions are small (<50 lines)
- [ ] Files are focused (<800 lines)
- [ ] Classes have single responsibility

### Code Structure:
- [ ] No deep nesting (>4 levels) - use early returns or extract functions
- [ ] Proper error handling with Result type
- [ ] Errors logged with appropriate context

### Clean Code:
- [ ] No println/console.log/Log.d statements in production code
- [ ] No hardcoded values - use named constants
- [ ] No magic numbers - define meaningful constant names

### Immutability:
- [ ] No mutation of shared state
- [ ] Use `copy()` for data class modifications
- [ ] Use immutable collections where possible
- [ ] StateFlow updates use `update {}` pattern
