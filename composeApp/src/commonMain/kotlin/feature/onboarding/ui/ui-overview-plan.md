# UI Overview Plan - Onboarding Feature

## Requirements Clarification Summary

### UI/UX Details:
- **Login Screen Design**: Reference image provided (shows email, password fields, login button)
- **Loading State**: Full transparent semi-black overlay with spinner (blocks interaction during login)
- **Error Display**:
  - Backend/server errors → Red snackbar
  - Validation errors → Inline error on EditText (below input field)
- **Password Visibility Toggle**: Yes (eye icon to show/hide password)
- **Password Requirements**: Show requirements; validate with debounce after user stops typing
- **Navigation**:
  - If logged in → Navigate to `DashboardScreen.kt`
  - If not logged in → Show Login screen
  - After successful login → Close login, navigate to Dashboard

### Business Logic:
- **Email Validation**: Valid email format, inline error if invalid
- **Password Validation**: Alphanumeric (letters + numbers), min 6 chars, inline error if invalid
- **Input Trimming**: Whitespace trimmed automatically

---

## Proposed Changes

### UI Architecture

This feature will follow the **MVI (Model-View-Intent)** pattern:
- **State**: `LoginState` (data class holding UI state)
- **Events**: `LoginEvent` (sealed class for user actions)
- **Effects**: `LoginEffect` (sealed class for one-time side effects like navigation)
- **ViewModel**: `LoginViewModel` (manages state, processes events, triggers effects)
- **Screen**: `LoginScreen` (Composable UI)

---

### State Management

#### [NEW] [LoginState.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/ui/state/LoginState.kt)
```kotlin
data class LoginState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false
)
```

**Fields**:
- `email`: Current email input value
- `password`: Current password input value
- `emailError`: Error message for email field (null if valid)
- `passwordError`: Error message for password field (null if valid)
- `isLoading`: True during login API call (shows loading overlay)
- `isPasswordVisible`: True when password should be visible (eye icon toggled)

---

#### [NEW] [LoginEvent.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/ui/state/LoginEvent.kt)
```kotlin
sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    object TogglePasswordVisibility : LoginEvent()
    object LoginClicked : LoginEvent()
}
```

**Events**:
- `EmailChanged`: User types in email field
- `PasswordChanged`: User types in password field
- `TogglePasswordVisibility`: User clicks eye icon to show/hide password
- `LoginClicked`: User clicks login button

---

#### [NEW] [LoginEffect.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/ui/state/LoginEffect.kt)
```kotlin
sealed class LoginEffect {
    data class ShowError(val message: String) : LoginEffect()
    object NavigateToDashboard : LoginEffect()
}
```

**Effects**:
- `ShowError`: Show red snackbar with error message (for backend/network errors)
- `NavigateToDashboard`: Navigate to dashboard after successful login

---

### ViewModel

#### [NEW] [LoginViewModel.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/ui/viewmodel/LoginViewModel.kt)
```kotlin
class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()
    
    private val _effect = Channel<LoginEffect>()
    val effect: Flow<LoginEffect> = _effect.receiveAsFlow()
    
    fun onEvent(event: LoginEvent)
}
```

**Responsibilities**:
1. **Email/Password Change Events**:
   - Update state with new value
   - Validate input with debounce (500ms after user stops typing)
   - Update error state if validation fails
   
2. **Toggle Password Visibility**:
   - Toggle `isPasswordVisible` state
   
3. **Login Clicked**:
   - Validate both email and password
   - If validation fails → Update error states, don't proceed
   - If validation passes:
     - Set `isLoading = true`
     - Call `loginUseCase(email, password)`
     - Handle result:
       - Success → Emit `NavigateToDashboard` effect
       - Error (validation) → Update error states
       - Error (network/backend) → Emit `ShowError` effect, set `isLoading = false`

**Debounce Logic**:
```kotlin
private val emailDebounce = viewModelScope.launch {
    snapshotFlow { _state.value.email }
        .debounce(500)
        .collect { email ->
            validateEmail(email)
        }
}
```

---

### UI Components

#### [NEW] [LoginTextField.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/ui/components/LoginTextField.kt)
```kotlin
@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    errorMessage: String?,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: (() -> Unit)? = null,
    modifier: Modifier = Modifier
)
```

**Features**:
- Styled text field matching the design (email icon, lock icon)
- Shows error message below field in red text
- Password field has eye icon to toggle visibility
- Uses design system colors and typography

---

#### [NEW] [LoginButton.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/ui/components/LoginButton.kt)
```kotlin
@Composable
fun LoginButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
)
```

**Features**:
- Primary button styled to match design
- Disabled state when loading or inputs invalid
- Uses design system button styles

---

#### [NEW] [PasswordRequirements.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/ui/components/PasswordRequirements.kt)
```kotlin
@Composable
fun PasswordRequirements(
    password: String,
    modifier: Modifier = Modifier
)
```

**Features**:
- Shows password requirements as checklist
- ✅ Minimum 6 characters
- ✅ Contains letters (a-z, A-Z)
- ✅ Contains numbers (0-9)
- Each requirement shows:
  - ✅ Green checkmark if met
  - ❌ Gray/red X if not met

**Placement**: Below password field, above error message

---

#### [NEW] [LoadingOverlay.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/ui/components/LoadingOverlay.kt)
```kotlin
@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier
)
```

**Features**:
- Full-screen transparent semi-black overlay (60% opacity)
- Centered circular progress indicator
- Blocks all touch events when visible
- Animated fade in/out

---

### Screen Composables

#### [NEW] [LoginScreen.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/ui/screen/LoginScreen.kt)
```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = koinViewModel(),
    onNavigateToDashboard: () -> Unit
)
```

**Layout Structure**:
```
Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(height = 80.dp)
        
        // App Logo/Icon
        Image(...)
        
        Spacer(height = 16.dp)
        
        // Title
        Text("News Feedz", style = AppTypography.headlineLarge)
        
        // Subtitle
        Text("Catch up with the world", style = AppTypography.bodyMedium)
        
        Spacer(height = 48.dp)
        
        // Email Field
        LoginTextField(
            value = state.email,
            onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
            label = "Email",
            errorMessage = state.emailError,
            leadingIcon = { Icon(Res.drawable.ic_email, ...) }
        )
        
        Spacer(height = 16.dp)
        
        // Password Field
        LoginTextField(
            value = state.password,
            onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
            label = "Password",
            errorMessage = state.passwordError,
            isPassword = true,
            isPasswordVisible = state.isPasswordVisible,
            onTogglePasswordVisibility = { viewModel.onEvent(LoginEvent.TogglePasswordVisibility) },
            leadingIcon = { Icon(Res.drawable.ic_lock, ...) }
        )
        
        Spacer(height = 8.dp)
        
        // Password Requirements
        PasswordRequirements(password = state.password)
        
        Spacer(height = 32.dp)
        
        // Login Button
        LoginButton(
            text = "Login",
            onClick = { viewModel.onEvent(LoginEvent.LoginClicked) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
    
    // Loading Overlay
    LoadingOverlay(isLoading = state.isLoading)
    
    // Snackbar Host
    SnackbarHost(...)
}
```

**Effects Handling**:
```kotlin
LaunchedEffect(Unit) {
    viewModel.effect.collect { effect ->
        when (effect) {
            is LoginEffect.ShowError -> {
                // Show red snackbar
                snackbarHostState.showSnackbar(
                    message = effect.message,
                    backgroundColor = Color.Red
                )
            }
            is LoginEffect.NavigateToDashboard -> {
                onNavigateToDashboard()
            }
        }
    }
}
```

---

### Navigation Integration

#### [MODIFY] [App.kt or Root Composable](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/App.kt)
**Add Initial Route Logic**:
```kotlin
@Composable
fun App() {
    val checkLoginStatus = koinInject<CheckLoginStatusUseCase>()
    val isLoggedIn by remember { mutableStateOf(runBlocking { checkLoginStatus() }) }
    
    val startDestination = if (isLoggedIn) {
        Routes.Dashboard
    } else {
        Routes.Login
    }
    
    NavHost(startDestination = startDestination) {
        composable(Routes.Login) {
            LoginScreen(
                onNavigateToDashboard = {
                    navController.navigate(Routes.Dashboard) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.Dashboard) {
            DashboardScreen()
        }
        // ... other routes
    }
}
```

> [!IMPORTANT]
> Need to activate `compose_navigation` skill for proper navigation implementation.

---

#### [MODIFY] [Navigation Routes](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/navigation/Routes.kt)
**Add Login Route**:
```kotlin
object Routes {
    const val Login = "login"
    const val Dashboard = "dashboard"
    // ... existing routes
}
```

---

### Dependency Injection

#### [NEW] [OnboardingModule.kt](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/feature/onboarding/di/OnboardingModule.kt)
```kotlin
val onboardingModule = module {
    // Data Layer
    single<AuthRemoteDataSource> { AuthRemoteDataSourceImpl(get(), get()) }
    single<AuthLocalDataSource> { AuthLocalDataSourceImpl(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    
    // Domain Layer
    factory { LoginUseCase(get(), get(), get()) }
    factory { ValidateEmailUseCase() }
    factory { ValidatePasswordUseCase() }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { CheckLoginStatusUseCase(get()) }
    
    // Presentation Layer
    viewModel { LoginViewModel(get(), get(), get()) }
}
```

---

#### [MODIFY] [Main Koin Setup](file:///Users/frenskylee/Documents/git/kmpBoiler/composeApp/src/commonMain/kotlin/di/KoinInitializer.kt)
**Add onboardingModule**:
```kotlin
startKoin {
    modules(
        coreModule,
        newsModule,
        onboardingModule, // ADD THIS
        // ... other modules
    )
}
```

---

## Resources Needed

> [!NOTE]
> Activate `resource_management` skill for proper resource handling.

### Icons (to be added to resources):
- **Email Icon**: `ic_email.xml` (envelope icon)
- **Lock Icon**: `ic_lock.xml` (padlock icon)
- **Eye Icon**: `ic_eye.xml` (eye open)
- **Eye Off Icon**: `ic_eye_off.xml` (eye closed/crossed)

### Strings (to be added to strings.xml):
```xml
<string name="login_title">News Feedz</string>
<string name="login_subtitle">Catch up with the world</string>
<string name="email_label">Email</string>
<string name="password_label">Password</string>
<string name="login_button">Login</string>
<string name="error_invalid_email">Please enter a valid email address</string>
<string name="error_invalid_password">Password must contain letters and numbers, at least 6 characters</string>
<string name="error_empty_email">Email cannot be empty</string>
<string name="error_empty_password">Password cannot be empty</string>
<string name="error_network">Network error. Please check your connection.</string>
<string name="password_req_length">At least 6 characters</string>
<string name="password_req_letters">Contains letters</string>
<string name="password_req_numbers">Contains numbers</string>
```

---

## Verification Plan

### Automated Tests

#### Test Files to Create:

1. **[NEW]** `commonTest/kotlin/feature/onboarding/ui/viewmodel/LoginViewModelTest.kt`
   - ✅ Email changed → State updates
   - ✅ Password changed → State updates
   - ✅ Email validation (debounced) → Error state updates
   - ✅ Password validation (debounced) → Error state updates
   - ✅ Toggle password visibility → State updates
   - ✅ Login clicked (valid inputs) → Calls use case, navigates on success
   - ✅ Login clicked (invalid inputs) → Shows validation errors, doesn't call use case
   - ✅ Login clicked (network error) → Shows error effect
   - ✅ Login clicked (backend error) → Shows error effect with message
   - ✅ Loading state → True during login, false after

#### Running Tests:
```bash
cd /Users/frenskylee/Documents/git/kmpBoiler
./gradlew :composeApp:cleanAllTests :composeApp:allTests --tests "*onboarding.ui*"
```

---

### Manual Verification

#### UI/UX Testing Checklist:
1. **Email Validation**:
   - [ ] Type invalid email → Error shows after debounce
   - [ ] Type valid email → Error clears
   - [ ] Leave email empty → Error shows after debounce

2. **Password Validation**:
   - [ ] Type password < 6 chars → Error shows
   - [ ] Type only letters → Error shows
   - [ ] Type only numbers → Error shows
   - [ ] Type valid password (6+ chars, alphanumeric) → Error clears
   - [ ] Password requirements update in real-time

3. **Password Visibility**:
   - [ ] Click eye icon → Password becomes visible
   - [ ] Click again → Password becomes hidden

4. **Login Flow**:
   - [ ] Click login with invalid inputs → Validation errors show
   - [ ] Click login with valid inputs → Loading overlay shows
   - [ ] Successful login → Navigate to dashboard
   - [ ] Backend error → Red snackbar shows with error message
   - [ ] Network error → Red snackbar shows "Network error"

5. **Navigation**:
   - [ ] Fresh install (not logged in) → Login screen shows
   - [ ] After login → Dashboard shows
   - [ ] Kill app and reopen → Dashboard shows (token persists)
   - [ ] After logout (next sprint) → Login screen shows

6. **Loading State**:
   - [ ] During login → Cannot interact with UI (overlay blocks touch)
   - [ ] Loading spinner shows centered on screen
   - [ ] After success/error → Overlay disappears

#### Running Manual Tests:
```bash
cd /Users/frenskylee/Documents/git/kmpBoiler
# Android
./gradlew :composeApp:installDebug
# iOS
./gradlew :composeApp:iosSimulatorArm64Test
# Desktop
./gradlew :composeApp:run
```

---

## Impact Analysis

### Existing Components

#### Navigation:
- **App.kt**: Modified to check login status and set initial route
- **Routes**: Add Login route
- **Impact**: Dashboard is now protected (requires login)
- **Strategy**: Ensure existing navigation tests account for login gate

#### No Test Conflicts:
This is a new feature with no existing tests to update.

---

## Notes

- **Design Reference**: Match the provided login screen image
- **Accessibility**: Ensure text fields have proper content descriptions
- **Keyboard Handling**: Password field should have "Done" IME action that triggers login
- **State Persistence**: Don't persist login form state (email/password cleared on screen close)
- **Security**: Never log password values (plain or hashed)
- **Responsive Design**: Ensure UI works on different screen sizes (phone/tablet)
