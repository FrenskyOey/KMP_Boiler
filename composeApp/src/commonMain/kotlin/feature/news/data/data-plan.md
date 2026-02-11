# Data Layer Implementation Plan - News Detail

## Goal
Implement the data layer including Repository implementation, API integration, and local caching with Room.

## User Review Required
> [!IMPORTANT]
> - **ID Transformation**: `xid = id % 8` logic will be implemented in `NewsDetailApiService` or `RemoteDataSource` before calling the API.
> - **TypeConverters**: Custom TypeConverters will be created for `Author` and `List<NewsContent>` to store them as JSON strings in Room.
> - **Cache Policy**: `NetworkBoundResource` strategy: Fetch from DB (show immediately) -> Fetch from Network -> Update DB.

## Proposed Changes

### feature/news/data

#### [NEW] [NewsDetailApiService.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/api/NewsDetailApiService.kt)
- `getNewsDetail(id: Int): ArticleDetailResponse`
- Endpoint: `v1/details?xid={id % 8}`

#### [NEW] [NewsDetailDao.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/dao/NewsDetailDao.kt)
- `@Query("SELECT * FROM article_detail WHERE id = :id")`
- `@Upsert`

#### [NEW] [ArticleDetailEntity.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/model/entity/ArticleDetailEntity.kt)
- Table: `article_detail`
- Columns: `id, title, category, image, author (json), publishedAt, readTime, content (json), shareUrl`

#### [NEW] [ArticleDetailResponse.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/model/response/ArticleDetailResponse.kt)
- Matches JSON response structure.
- `content` field is `List<ContentItemResponse>`

#### [NEW] [NewsDetailRepositoryImpl.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/data/repository/NewsDetailRepositoryImpl.kt)
- Implements `NewsDetailRepository`
- Uses `NewsDetailDao` and `NewsDetailApiService`
- Logic:
  1. Emit local data from Dao.
  2. If network logical (always refresh), fetch from API.
  3. Map Response -> Entity -> Insert into Dao.
  4. Emit updated local data.

#### [MODIFY] [AppDatabase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/core/data/local/database/AppDatabase.kt)
- Add `ArticleDetailEntity` to `entities` list.
- Add `abstract fun newsDetailDao(): NewsDetailDao`

## Verification
- **Test**: `NewsDetailRepositoryImplTest` (TDD)
- **Verify**: Check `xid` modulo logic in tests. Verify JSON serialization for Room.
