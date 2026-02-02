# Domain Layer Plan - News Feed

## Requirements Clarification Summary
**Feature**: News Feed
**API Endpoint**: `https://cd841015-7e2e-4a18-8082-a7c23d45097e.mock.pstmn.io/v1/article?page={{page}}`
**Pagination**: Page-based. Empty list indicates end.
**Caching**: Upsert on success in Room DB.
**UI**: List with Title, Content, Image, Topic.

## Overview
- **Feature**: Display a paginated list of news articles.
- **Business Logic**: Fetch data from repository, handling pagination and error mapping.

## 1. Models
### 1.1 Data Models
- **Article**: Core domain model.
    - `id`: Long
    - `title`: String
    - `content`: String
    - `imageUrl`: String
    - `topic`: String

### 1.2 Error Models
- **AppException**: Defined in Core, reused here (NetworkError, ServerError, etc).

## 2. Repository Interface
### 2.1 Contract Definition
`shared/src/commonMain/kotlin/feature/news/domain/repository/NewsFeedRepository.kt`

```kotlin
import core.domain.model.Result

interface NewsFeedRepository {
    fun getArticles(page: Int): Flow<Result<List<Article>>>
    fun getArticleCount(): Flow<Int>
}
```

## 3. Use Cases
### 3.1 GetNewsFeedUseCase
`shared/src/commonMain/kotlin/feature/news/domain/usecase/GetNewsFeedUseCase.kt`

- **Responsibility**: Fetch news articles for a specific page.
- **Input**: `page: Int`
- **Output**: `Flow<Result<List<Article>>>`

## 4. Test Cases
### 4.1 Use Case Tests
- ✓ Test successful data retrieval (emits Success)
- ✓ Test error handling (emits Error)
- ✓ Test pagination (verify different pages called)

## 5. Implementation Checklist
- [ ] Create `Article` data model
- [ ] Define `NewsFeedRepository` interface
- [ ] Implement `GetNewsFeedUseCase`
- [ ] Write `GetNewsFeedUseCaseTest`
- [ ] Verify all tests pass
