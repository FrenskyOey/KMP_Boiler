---
description: Testing guidelines and code review checklist
---

# Testing Rules

## Test Structure

```kotlin
// Tests mirror the source structure
shared/src/commonTest/kotlin/
├── core/
│   └── data/
│       └── util/
│           └── ApiErrorHandlerTest.kt
└── feature/
    ├── news/
    │   ├── domain/
    │   │   └── usecase/
    │       └── GetNewsFeedUseCaseTest.kt
    │   └── data/
    │       └── repository/
    │           └── NewsFeedRepositoryImplTest.kt
    └── settings/
        └── domain/
        └── usecase/
            └── GetSettingsUseCaseTest.kt
```

## Test Isolation

```kotlin
// ✅ CORRECT - Test only the feature being tested
class GetNewsFeedUseCaseTest {
    private lateinit var fakeRepository: FakeNewsFeedRepository
    private lateinit var useCase: GetNewsFeedUseCase
    
    @Before
    fun setup() {
        fakeRepository = FakeNewsFeedRepository()
        useCase = GetNewsFeedUseCase(fakeRepository)
    }
}

// ❌ WRONG - Test depends on another feature
class GetNewsFeedUseCaseTest {
    private lateinit var settingsRepository: SettingsRepository  // WRONG!
}
```

# Code Review Checklist

Before accepting code, verify:

### Architecture:
- [ ] Features don't depend on each other
- [ ] Features only use core for shared utilities
- [ ] Each feature has data/domain/di structure
- [ ] Shared utilities are in core, not features
- [ ] Each feature has its own Koin module

### Code Quality:
- [ ] Follows Clean Architecture within feature
- [ ] Uses dependency injection (Koin)
- [ ] Has proper error handling with core Result
- [ ] Maps between DTOs/Entities and Domain models
- [ ] Uses suspend functions for async operations
- [ ] Has proper state management
- [ ] Follows Kotlin coding conventions
- [ ] No hardcoded values
- [ ] **No hardcoded URLs - uses AppConfig**
- [ ] **All API services inject AppConfig**
- [ ] Uses theme helpers
- [ ] Has appropriate documentation

### Feature Isolation:
- [ ] No imports from other features
- [ ] All dependencies injected via Koin
- [ ] Can be removed without breaking other features
- [ ] Has its own tests
