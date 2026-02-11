# Domain Layer Implementation Plan - News Detail

## Goal
Implement the domain layer for the News Detail feature, enabling the retrieval of detailed article information.

## User Review Required
> [!IMPORTANT]
> - **New Model**: `NewsDetail` will be created separate from `Article` to accommodate the richer data structure (content list, author, etc.).
> - **Logic**: The `xid` calculation (`id % 8`) will be handled in the Data Layer, not Domain. Domain simply requests by `id`.

## Proposed Changes

### feature/news/domain

#### [NEW] [NewsDetail.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/domain/model/NewsDetail.kt)
- **Model**: `NewsDetail`
- **Fields**:
  - `id: Int`
  - `title: String`
  - `category: String`
  - `image: String` (mapped from `image` or `imageUrl`)
  - `author: Author` (Nested class with name, avatar, publication)
  - `publishedAt: String`
  - `readTime: Int`
  - `content: List<NewsContent>` (Sealed Interface: Paragraph, Quote)
  - `shareUrl: String`

#### [NEW] [NewsDetailRepository.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/domain/repository/NewsDetailRepository.kt)
- **Interface**: `NewsDetailRepository`
- **Functions**:
  - `getNewsDetail(id: Int): Flow<Result<NewsDetail>>`

#### [NEW] [GetNewsDetailUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/news/domain/usecase/GetNewsDetailUseCase.kt)
- **UseCase**: `GetNewsDetailUseCase`
- **Logic**: Invokes repository `getNewsDetail(id)`.

## Verification
- **Test**: `GetNewsDetailUseCaseTest` (TDD)
- **Verify**: Ensure generic `Result` type is correctly propagated.
