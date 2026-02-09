---
name: data_implementation
description: Enforces standardized Data Layer patterns. Use when implementing Repositories, Data Sources, or API Services.
---

# Data Layer Implementation Skill

Use this skill when implementing the Data Layer (Repositories, Data Sources, API Services) to ensure consistency and robustness.

## 1. Directory & Package Structure

Mandatory structure for `feature/[name]/data/`:

```text
data/
├── api/             # API Service definitions & implementations
│   ├── [Name]ApiService.kt      (Interface)
│   └── [Name]ApiServiceImpl.kt  (Implementation)
├── datasource/      # Data Source interfaces & implementations
│   ├── [Name]DataSource.kt      (Parent Interface)
│   ├── remote/
│   │   └── [Name]RemoteDataSourceImpl.kt
│   └── local/
│       └── [Name]LocalDataSourceImpl.kt
├── model/           # Data Transfer Objects & Entities
│   ├── entity/      # Local database/storage models
│   ├── request/     # Network request bodies
│   ├── response/    # Network response bodies
│   └── mapper/      # Mappers (DTO <-> Domain <-> Entity)
└── repository/      # Repository Implementation
    └── [Name]RepositoryImpl.kt
```

## 2. API Service Pattern

**Decouple Networking Logic**:
- Define a pure interface `[Feature]ApiService`.
- Implement it in `[Feature]ApiServiceImpl` taking `HttpClient`.
- **NEVER** use `HttpClient` directly in `RemoteDataSource`.

```kotlin
// Interface
interface AuthApiService {
    suspend fun login(request: LoginRequest): LoginResponse
}

// Implementation
class AuthApiServiceImpl(private val client: HttpClient) : AuthApiService { ... }
```

## 3. Data Source Pattern

**Nested Interfaces**:
- Use a single parent interface to group Remote and Local definitions.

```kotlin
interface AuthDataSource {
    interface Remote {
        suspend fun login(request: LoginRequest): LoginResponse
    }
    interface Local {
        fun saveToken(token: String)
    }
}
```

## 4. Error Handling Rules

**Trigger**: When handling exceptions in Data Layer components.

- **RemoteDataSource**:
    - **Catch** network exceptions.
    - **Map** to `AppException` (using `ApiErrorHandler` if applicable).
    - **THROW** the `AppException`.
    - *Do NOT return Result.Error here.*

```kotlin
// RemoteDataSourceImpl
try {
    // ... api call ...
} catch (e: Exception) {
    throw ApiErrorHandler.handleError(e) // THROW!
}
```

- **Repository**:
    - **Catch** exceptions from Data Sources.
    - **Map** unknown exceptions to `AppException.Unknown`.
    - **RETURN** `Result.Error`.

```kotlin
// RepositoryImpl
try {
    val response = remoteDataSource.login(...)
    Result.Success(response.toDomain())
} catch (e: Exception) {
    // Propagate existing AppExceptions (from DataSource) or wrap others
    val error = (e as? AppException) ?: AppException.UnknownError(e.message, e)
    Result.Error(error) // RETURN!
}
```

## 5. Implementation Workflow

1.  **Define Models**: Create all `request`, `response`, `entity` classes first.
2.  **Define API Interface**: Create the contract.
3.  **Define Data Sources**: Update the definitions.
4.  **TDD**: Write tests for `RemoteDataSourceImplTest` and `RepositoryImplTest` **before** implementation.
5.  **Implement**:
    - `ApiServiceImpl`
    - `RemoteDataSourceImpl` & `LocalDataSourceImpl`
    - `RepositoryImpl`
    - `Mappers`
6.  **DI**: Register all in `[Feature]Module`.
