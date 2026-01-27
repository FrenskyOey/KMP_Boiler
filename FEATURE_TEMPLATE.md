# How to Add a New Feature

## Step 1: Create Directory Structure
Create the feature directory in `shared/src/commonMain/kotlin/feature/[feature_name]/`.
Subdirectories needed:
- `data/`
    - `remote/` (API services)
    - `repository/` (Implementations)
    - `mapper/` (DTO to Domain mappers)
- `domain/`
    - `model/` (Pure data classes)
    - `repository/` (Interfaces)
    - `usecase/` (Business logic)
- `di/` (Koin module)

## Step 2: Define Domain Layer
1. Create models in `domain/model/`.
2. Define `Repository` interface in `domain/repository/`.
3. Create `UseCase` classes in `domain/usecase/`.

## Step 3: Implement Data Layer
1. Create DTOs if API differs from Domain.
2. Implement Repository in `data/repository/`.
3. Create API Service/DataSource in `data/remote/`.

## Step 4: Dependency Injection
Create `[FeatureName]Module.kt` in `di/`.
```kotlin
val featureNameModule = module {
    single<Repository> { RepositoryImpl(get()) }
    factory { UseCase(get()) }
}
```

## Step 5: Android UI (Compose)
Create UI in `composeApp/src/commonMain/kotlin/feature/[feature_name]/`.
- `Screen.kt` (Composable)
- `ViewModel.kt` (Shared ViewModel)
- `components/` (Local components)

## Step 6: Navigation
Update `composeApp/src/commonMain/kotlin/core/navigation/Screen.kt` and `NavGraph.kt`.

## Step 7: Register Module
Add the feature Koin module to `MyApp.kt` or the main module aggregator.
