# Data Layer Plan - Onboarding Feature

## Requirements Clarification Summary

### API Details:
- **Endpoint**: `POST https://cd841015-7e2e-4a18-8082-a7c23d45097e.mock.pstmn.io/v1/login`
- **Base URL**: From AppConfig (mock Postman URL for now)
- **Request Format**: `x-www-form-urlencoded`
  - `userName`: emails (case-sensitive)
  - `password`: MD5 hash
- **Success Response**: `{ data: { user_name, user_id, token }, is_success: true }`
- **Error Response**: `{ data: null, error_message: "...", is_success: false }`

### Storage:
- **Secure Storage**: Token + user data stored in encrypted preferences
- **Token Injection**: Add token to all API calls as `Authorization: Bearer{token}` header
- **Clear on Logout**: Remove all stored auth data

### Network Error Handling:
- Connection issues → Return network error
- Backend errors → Return error with error_message from response

---

## Proposed Changes

### Data Models

#### [NEW] [LoginRequest.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/data/model/LoginRequest.kt)
```kotlin
data class LoginRequest(
    val userName: String, // email
    val password: String  // MD5 hashed password
)
```

**Purpose**: Request DTO for login endpoint (will be serialized as x-www-form-urlencoded).

---

#### [NEW] [LoginResponse.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/data/model/LoginResponse.kt)
```kotlin
@Serializable
data class LoginResponse(
    @SerialName("data") val data: UserData?,
    @SerialName("is_success") val isSuccess: Boolean,
    @SerialName("error_message") val errorMessage: String? = null
)

@Serializable
data class UserData(
    @SerialName("user_name") val userName: String,
    @SerialName("user_id") val userId: Int,
    @SerialName("token") val token: String
)
```

**Purpose**: Response DTO matching API JSON structure.

---

#### [NEW] [UserEntity.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/data/model/UserEntity.kt)
```kotlin
@Serializable
data class UserEntity(
    val userId: Int,
    val userName: String,
    val token: String
)
```

**Purpose**: Local storage entity for persisting user data in secure storage.

---

### Mappers

#### [NEW] [LoginMapper.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/data/mapper/LoginMapper.kt)
```kotlin
// UserData (API) -> User (Domain)
fun UserData.toDomain(): User {
    return User(
        userId = this.userId,
        userName = this.userName,
        token = this.token
    )
}

// User (Domain) -> UserEntity (Storage)
fun User.toEntity(): UserEntity {
    return UserEntity(
        userId = this.userId,
        userName = this.userName,
        token = this.token
    )
}

// UserEntity (Storage) -> User (Domain)
fun UserEntity.toDomain(): User {
    return User(
        userId = this.userId,
        userName = this.userName,
        token = this.token
    )
}
```

---

### Data Sources

#### [NEW] [AuthRemoteDataSource.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/data/datasource/remote/AuthRemoteDataSource.kt)
```kotlin
interface AuthRemoteDataSource {
    suspend fun login(request: LoginRequest): LoginResponse
}

class AuthRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val appConfig: AppConfig
) : AuthRemoteDataSource {
    override suspend fun login(request: LoginRequest): LoginResponse
}
```

**Responsibilities**:
1. Inject `AppConfig` to get base URL
2. Construct endpoint: `${appConfig.baseApiUrl}/v1/login`
3. Make POST request with `x-www-form-urlencoded` body
4. Parse `LoginResponse` from JSON
5. Throw appropriate exceptions for network/HTTP errors

**Ktor Configuration**:
- Use `FormDataContent` for `x-www-form-urlencoded` encoding
- Content-Type: `application/x-www-form-urlencoded`

---

#### [NEW] [AuthLocalDataSource.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/data/datasource/local/AuthLocalDataSource.kt)
```kotlin
interface AuthLocalDataSource {
    suspend fun saveUser(userEntity: UserEntity)
    suspend fun getUser(): UserEntity?
    suspend fun clearUser()
    suspend fun getToken(): String?
}

class AuthLocalDataSourceImpl(
    private val secureStorage: SecureStorage
) : AuthLocalDataSource {
    companion object {
        private const val KEY_USER_DATA = "user_data"
    }
}
```

**Responsibilities**:
1. Use `SecureStorage` (EncryptedSharedPreferences/Keychain) to store user data
2. Serialize `UserEntity` to JSON before storing
3. Deserialize JSON back to `UserEntity` on retrieval
4. Provide methods to save, get, clear user data
5. Provide quick token getter for auth header injection

> [!IMPORTANT]
> `SecureStorage` is a platform-specific implementation (expect/actual pattern):
> - **Android**: EncryptedSharedPreferences
> - **iOS**: Keychain Services
> 
> This should be implemented in `core/data/local/` if it doesn't exist yet.

---

### Repository Implementation

#### [NEW] [AuthRepositoryImpl.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/data/repository/AuthRepositoryImpl.kt)
```kotlin
class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {
    override suspend fun login(credentials: LoginCredentials): Result<User>
    override suspend fun logout(): Result<Unit>
    override suspend fun getCurrentUser(): Result<User?>
    override suspend fun isLoggedIn(): Boolean
}
```

**Implementation Details**:

**`login(credentials)`**:
1. Create `LoginRequest` with credentials (email + hashed password)
2. Call `remoteDataSource.login(request)`
3. Handle response:
   - If `isSuccess == false` → Return `Result.Error(ApiException(errorMessage))`
   - If `isSuccess == true && data != null` → Map `data.toDomain()`, save to local, return `Result.Success(user)`
   - If `data == null` → Return error
4. Catch network exceptions → Return `Result.Error(NetworkException)`

**`logout()`**:
1. Call `localDataSource.clearUser()`
2. Return `Result.Success(Unit)`

**`getCurrentUser()`**:
1. Call `localDataSource.getUser()`
2. Map to domain if exists
3. Return `Result.Success(user)` or `Result.Success(null)`

**`isLoggedIn()`**:
1. Check if `localDataSource.getToken()` is not null
2. Return boolean (no need for Result wrapper)

---

### Core Infrastructure Updates

#### [MODIFY/NEW] [SecureStorage.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/core/data/local/SecureStorage.kt)
```kotlin
// commonMain
expect class SecureStorage {
    suspend fun save(key: String, value: String)
    suspend fun get(key: String): String?
    suspend fun remove(key: String)
    suspend fun clear()
}
```

**Platform Implementations**:
- **androidMain**: Use `EncryptedSharedPreferences`
- **iosMain**: Use `NSUserDefaults` with Keychain or platform-specific secure storage
- **desktopMain**: Use encrypted file storage or Java KeyStore

> [!NOTE]
> If `SecureStorage` already exists in `core/`, verify it has the methods needed. If not, this will be a **NEW** component.

---

#### [MODIFY] [HttpClient Setup](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/core/data/remote/HttpClientFactory.kt)
**Add Auth Interceptor**:
```kotlin
install(Auth) {
    bearer {
        loadTokens {
            // Inject token from AuthLocalDataSource
            val token = authLocalDataSource.getToken()
            token?.let {
                BearerTokens(accessToken = it, refreshToken = "")
            }
        }
    }
}
```

**OR** use a custom interceptor:
```kotlin
install(HttpSend) {
    addPhase(HttpSendPhase.State)
    intercept { request ->
        val token = authLocalDataSource.getToken()
        if (token != null) {
            request.headers.append("Authorization", "Bearer $token")
        }
        proceed(request)
    }
}
```

> [!WARNING]
> **Breaking Change**: This modifies the global `HttpClient` to inject auth tokens into ALL requests.
> - **Impact**: All API calls will now include `Authorization` header if user is logged in
> - **Strategy**: This is the desired behavior, but verify existing features (news) still work correctly

---

#### [NEW] [HashingUtil.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/core/util/HashingUtil.kt)
```kotlin
expect object HashingUtil {
    fun md5(input: String): String
}
```

**Platform Implementations**:
- **androidMain**: Use `java.security.MessageDigest`
- **iosMain**: Use `CommonCrypto` or Kotlin crypto library
- **desktopMain**: Use `java.security.MessageDigest`

**Purpose**: Convert plain password to MD5 hash before sending to backend.

---

#### [MODIFY] [AppException.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/core/domain/model/AppException.kt)
Add new exception types:
```kotlin
data class ValidationException(val validationError: ValidationError) : AppException()
data class AuthException(override val message: String) : AppException()
```

---

## Verification Plan

### Automated Tests

All tests will be written FIRST (TDD approach) before implementation.

#### Test Files to Create:

1. **[NEW]** `commonTest/kotlin/feature/onboarding/data/datasource/remote/AuthRemoteDataSourceImplTest.kt`
   - ✅ Successful login → Returns LoginResponse with data
   - ✅ Failed login → Returns LoginResponse with error_message
   - ✅ Network error → Throws exception
   - ✅ Verify correct endpoint construction (uses AppConfig)
   - ✅ Verify request format (x-www-form-urlencoded)

2. **[NEW]** `commonTest/kotlin/feature/onboarding/data/datasource/local/AuthLocalDataSourceImplTest.kt`
   - ✅ Save user → Can retrieve user
   - ✅ Clear user → Returns null after clear
   - ✅ Get token → Returns token from stored user
   - ✅ No user stored → Returns null

3. **[NEW]** `commonTest/kotlin/feature/onboarding/data/repository/AuthRepositoryImplTest.kt`
   - ✅ Login success → Saves user locally and returns Result.Success
   - ✅ Login failure (isSuccess=false) → Returns Result.Error with message
   - ✅ Network error → Returns Result.Error(NetworkException)
   - ✅ Logout → Clears local data
   - ✅ isLoggedIn → Returns true when token exists, false otherwise
   - ✅ getCurrentUser → Returns cached user

4. **[NEW]** `commonTest/kotlin/feature/onboarding/data/mapper/LoginMapperTest.kt`
   - ✅ UserData.toDomain() maps correctly
   - ✅ User.toEntity() maps correctly
   - ✅ UserEntity.toDomain() maps correctly

5. **[NEW]** `commonTest/kotlin/core/util/HashingUtilTest.kt`
   - ✅ MD5 hashing produces correct output
   - ✅ Same input produces same hash

#### Running Tests:
```bash
cd /Users/frenskylee/Documents/git/kmpBoiler
./gradlew :composeApp:cleanAllTests :composeApp:allTests --tests "*onboarding.data*"
```

---

### Manual Verification

#### Test HTTP Client Auth Interceptor:
1. Login with valid credentials
2. Make a request to news API (or any existing endpoint)
3. Verify in network logs that `Authorization: Bearer{token}` header is present
4. Logout
5. Make another request
6. Verify `Authorization` header is NOT present

**How to verify**:
- Enable Ktor logging in HttpClient
- Check terminal/logcat for request headers

---

## Impact Analysis

### Existing Tests/Components

#### Potentially Affected:
- **HttpClient configuration** (adding auth interceptor)
  - **Impact**: All existing API calls will now include Authorization header when user is logged in
  - **Strategy**: This is desired behavior. Verify existing features (news) still work.

#### New Core Components:
- **SecureStorage** (if doesn't exist) → Shared by all features needing secure storage
- **HashingUtil** → Can be used by other features in future

### Dependency Updates Needed:
- `kotlinx-serialization` → For JSON serialization (likely already included)
- Ktor Auth plugin → For bearer token injection
- Platform-specific crypto libraries → For MD5 hashing

---

## Notes

- **Security**: Never log token or password (plain or hashed)
- **Token Format**: Backend response includes "Bearer" prefix in token, may need to strip/add accordingly
- **Storage**: Use platform-specific secure storage (EncryptedSharedPreferences on Android, Keychain on iOS)
- **Error Handling**: Distinguish between validation errors, network errors, and API errors
- **Testing**: Use MockEngine for Ktor client tests, FakeSecureStorage for repository tests
