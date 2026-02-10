# Domain Layer Plan - Onboarding Feature

## Requirements Clarification Summary

### API Details:
- **Endpoint**: `POST https://cd841015-7e2e-4a18-8082-a7c23d45097e.mock.pstmn.io/v1/login`
- **Base URL**: Configured via AppConfig (mock Postman for now)
- **Request**: `x-www-form-urlencoded` with `userName` (email) and `password` (MD5 hash)
- **Response**: Success returns token, user_name, user_id; Error returns error_message

### Storage:
- Token stored securely and injected into API headers as `Authorization: Bearer{token}`
- User data (user_name, user_id, token) stored in secure storage (encrypted preferences)
- Token cleared on logout

### Business Logic:
- Email validation: Must be valid email format
- Password validation: Alphanumeric (letters + numbers), minimum 6 characters
- Password hashing: MD5 before sending to backend
- Input trimming: Whitespace trimmed from email/password
- Email is case-sensitive
- Logout functionality: Domain/data designed now (UI in next sprint)

---

## Proposed Changes

### Domain Models

#### [NEW] [User.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/model/User.kt)
```kotlin
data class User(
    val userId: Int,
    val userName: String,
    val token: String
)
```

**Purpose**: Represents authenticated user information in domain layer.

---

#### [NEW] [LoginCredentials.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/model/LoginCredentials.kt)
```kotlin
data class LoginCredentials(
    val email: String,
    val password: String
)
```

**Purpose**: Encapsulates login input data with validation.

---

#### [NEW] [ValidationError.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/model/ValidationError.kt)
```kotlin
sealed class ValidationError {
    object InvalidEmail : ValidationError()
    object InvalidPassword : ValidationError() // Not alphanumeric or < 6 chars
    object EmptyEmail : ValidationError()
    object EmptyPassword : ValidationError()
}
```

**Purpose**: Represents validation errors for user inputs.

---

### Repository Interface

#### [NEW] [AuthRepository.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/repository/AuthRepository.kt)
```kotlin
interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
    suspend fun isLoggedIn(): Boolean
}
```

**Purpose**: Abstraction for authentication operations.

**Operations**:
- `login`: Authenticates user with credentials, returns User with token
- `logout`: Clears stored token and user data
- `getCurrentUser`: Retrieves cached user data if logged in
- `isLoggedIn`: Checks if valid token exists

---

### Use Cases

#### [NEW] [LoginUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/usecase/LoginUseCase.kt)
```kotlin
class LoginUseCase(
    private val authRepository: AuthRepository,
    private val validateEmail: ValidateEmailUseCase,
    private val validatePassword: ValidatePasswordUseCase
) {
    suspend operator fun invoke(email: String, password: String): Result<User>
}
```

**Responsibilities**:
1. Trim email and password inputs
2. Validate email format (delegate to ValidateEmailUseCase)
3. Validate password (alphanumeric + min 6 chars) (delegate to ValidatePasswordUseCase)
4. Create LoginCredentials with hashed password (MD5)
5. Call repository.login()
6. Return Result<User> or error

**Error Cases**:
- `ValidationError` if inputs are invalid
- `NetworkError` if server connection fails
- `ApiError` if backend returns error_message

---

#### [NEW] [ValidateEmailUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/usecase/ValidateEmailUseCase.kt)
```kotlin
class ValidateEmailUseCase {
    operator fun invoke(email: String): Result<Unit>
}
```

**Validation Rules**:
- Not empty
- Valid email format (contains @, valid structure)
- Case-sensitive (no normalization)

**Returns**: `Result.Success(Unit)` or `Result.Error(ValidationError.InvalidEmail)`

---

#### [NEW] [ValidatePasswordUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/usecase/ValidatePasswordUseCase.kt)
```kotlin
class ValidatePasswordUseCase {
    operator fun invoke(password: String): Result<Unit>
}
```

**Validation Rules**:
- Not empty
- Minimum 6 characters
- Contains at least one letter (a-z, A-Z)
- Contains at least one number (0-9)

**Returns**: `Result.Success(Unit)` or `Result.Error(ValidationError.InvalidPassword)`

---

#### [NEW] [LogoutUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/usecase/LogoutUseCase.kt)
```kotlin
class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit>
}
```

**Responsibilities**:
- Call repository.logout() to clear token and user data
- Return success/error result

> [!NOTE]
> UI for logout button will be added in next sprint (Settings screen), but domain/data designed now.

---

#### [NEW] [GetCurrentUserUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/usecase/GetCurrentUserUseCase.kt)
```kotlin
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User?>
}
```

**Responsibilities**:
- Retrieve cached user data from repository
- Used for checking login state on app start

---

#### [NEW] [CheckLoginStatusUseCase.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/domain/usecase/CheckLoginStatusUseCase.kt)
```kotlin
class CheckLoginStatusUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Boolean
}
```

**Responsibilities**:
- Quick check if user is logged in (token exists)
- Used for navigation routing on app start

---

## Verification Plan

### Automated Tests

All tests will be written FIRST (TDD approach) before implementation.

#### Test Files to Create:
1. **[NEW]** `commonTest/kotlin/feature/onboarding/domain/usecase/LoginUseCaseTest.kt`
   - ✅ Valid credentials → Success
   - ✅ Invalid email → ValidationError.InvalidEmail
   - ✅ Invalid password → ValidationError.InvalidPassword
   - ✅ Empty email → ValidationError.EmptyEmail
   - ✅ Empty password → ValidationError.EmptyPassword
   - ✅ Network error → NetworkError
   - ✅ Backend error → ApiError with message
   - ✅ Input trimming verification

2. **[NEW]** `commonTest/kotlin/feature/onboarding/domain/usecase/ValidateEmailUseCaseTest.kt`
   - ✅ Valid email → Success
   - ✅ Invalid email (no @) → Error
   - ✅ Invalid email (malformed) → Error
   - ✅ Empty email → Error
   - ✅ Case sensitivity check

3. **[NEW]** `commonTest/kotlin/feature/onboarding/domain/usecase/ValidatePasswordUseCaseTest.kt`
   - ✅ Valid password (alphanumeric, 6+ chars) → Success
   - ✅ Only letters → Error
   - ✅ Only numbers → Error
   - ✅ Less than 6 chars → Error
   - ✅ Empty password → Error

4. **[NEW]** `commonTest/kotlin/feature/onboarding/domain/usecase/LogoutUseCaseTest.kt`
   - ✅ Logout calls repository.logout()
   - ✅ Returns success result

5. **[NEW]** `commonTest/kotlin/feature/onboarding/domain/usecase/GetCurrentUserUseCaseTest.kt`
   - ✅ Returns cached user when logged in
   - ✅ Returns null when not logged in

6. **[NEW]** `commonTest/kotlin/feature/onboarding/domain/usecase/CheckLoginStatusUseCaseTest.kt`
   - ✅ Returns true when token exists
   - ✅ Returns false when no token

#### Running Tests:
```bash
cd /Users/frenskylee/Documents/git/kmpBoiler
./gradlew :composeApp:cleanAllTests :composeApp:allTests --tests "*onboarding.domain*"
```

---

## Impact Analysis

### Existing Tests/Components
This is a **NEW** feature. No existing tests will be broken.

### New Dependencies
- Core utilities that may be needed:
  - `core/domain/model/Result.kt` (already exists for handling Success/Error)
  - `core/domain/model/AppException.kt` (may need new error types)
  - MD5 hashing utility (needs to be added to `core/util/`)

### Shared Components to Create/Modify:
- **[NEW]** `core/util/HashingUtil.kt` - MD5 hashing function
- **[MODIFY]** `core/domain/model/AppException.kt` - Add `ValidationException`, `AuthException` if needed

---

## Notes

- All domain logic is pure Kotlin (no platform dependencies)
- Use cases follow single responsibility principle
- Repository interface provides abstraction for data layer
- Validation errors are explicit and type-safe
- MD5 hashing will be done in use case before passing to repository
