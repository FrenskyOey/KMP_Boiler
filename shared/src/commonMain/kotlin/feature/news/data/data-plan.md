# Data Layer Plan - News Feed

## Overview
- **API Endpoint**: `https://cd841015-7e2e-4a18-8082-a7c23d45097e.mock.pstmn.io/v1/article`
- **Response Format**: JSON containing `data` list and status.
- **Caching Strategy**: Room Database (`Upsert` on success). "Always Online" hybrid (fetch remote, save local).

## 1. Response Models & DTOs
### 1.1 API Response Model
```kotlin
@Serializable
data class ArticleResponseDto(
    @SerialName("data") val data: List<ArticleDto>?,
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("error_message") val errorMessage: String?
)
```

### 1.2 DTO Models
- **ArticleDto**: Maps JSON fields (`id`, `title`, `content`, `img`, `topic`).

### 1.3 Mapping Functions
- `ArticleDto.toDomain(): Article`
- `ArticleDto.toEntity(): ArticleEntity`

## 2. Data Source Abstractions
### 2.1 Remote Data Source
```kotlin
interface NewsRemoteDataSource {
    suspend fun fetchArticles(page: Int): List<ArticleDto>
}
```

### 2.2 Local Data Source
```kotlin
interface NewsLocalDataSource {
    fun getAllArticles(): Flow<List<ArticleEntity>>
    fun getArticleCount(): Flow<Int>
    suspend fun upsertArticles(articles: List<ArticleEntity>)
    suspend fun clearArticles()
}
```

## 3. Implementation Details
### 3.1 Remote Implementation
- **HTTP Client**: Ktor.
- **Service**: `NewsApiService`.
- **Implementation**: `NewsRemoteDataSourceImpl` calls service, checks `isSuccess`, throws exception if failed.

### 3.2 Local Implementation
- **Storage**: Room.
- **Entity**: `ArticleEntity` (table: `article_entity`).
- **DAO**: `NewsDao` (`@Upsert`, `@Query`).
- **Implementation**: `NewsLocalDataSourceImpl`.

### 3.3 Repository Implementation (`NewsFeedRepositoryImpl`)
- `getArticles(page)`:
    - Emit `Result.Loading`.
    - Try:
        - Fetch Remote.
        - Map to Entity -> Upsert Local.
        - Map to Domain -> Emit `Result.Success`.
    - Catch:
        - Emit `Result.Error` (mapped to `AppException`).
- `getArticleCount()`: Delegate to LocalDataSource.

## 4. Test Cases
### 4.1 Remote Data Source Tests
- ✓ Test successful API call
- ✓ Test API error (isSuccess=false)

### 4.2 Repository Tests
- ✓ Test successful fetch saves to DB
- ✓ Test network error emits Error result

## 5. Implementation Checklist
- [ ] Create `ArticleResponseDto` & `ArticleDto`
- [ ] Implement `NewsApiService` & `NewsRemoteDataSource`
- [ ] Create `ArticleEntity` & `NewsDao`
- [ ] Implement `NewsLocalDataSource`
- [ ] Implement `NewsFeedRepositoryImpl`
- [ ] Write Unit Tests
- [ ] Verify all tests pass
