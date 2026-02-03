---
description: High-level architectural rules and cross-feature boundaries
---

# Feature-First Architecture Rules

## Project Context
This is a Kotlin Multiplatform (KMP) project with **Feature-First Modular Architecture**. Even though code resides in a single module (`composeApp`), we maintain strict logical boundaries between features.

## Feature Structure
```
composeApp/src/commonMain/kotlin/
├── core/               # Shared utilities & Infrastructure
│   ├── data/
│   ├── domain/
│   ├── di/
│   └── util/
└── feature/            # Feature modules
    ├── news/
    │   ├── data/
    │   ├── domain/
    │   ├── di/
    │   └── ui/         # Presentation Layer
    └── settings/
        ├── data/
        ├── domain/
        ├── di/
        └── ui/
```

## CRITICAL Dependency Rules

**✅ ALLOWED:**
- Features can always depend on `core/`
- features/ui can depend on feature/domain
- features/data can depend on feature/domain
- Features can depend on external libraries
- UI layer can depend on any feature (via Navigation)

**❌ FORBIDDEN:**
- Features CANNOT depend on other features' internal implementation
- Features CANNOT import from other features' `data` or `ui` packages directly
- `domain` layer CANNOT depend on `ui` or `data`

**Example:**
```kotlin
// ✅ CORRECT - Feature depends on core
import core.domain.model.Result
import feature.news.domain.model.NewsFeed

// ❌ WRONG - Feature depends on another feature's implementation details
import feature.settings.data.repository.SettingsRepositoryImpl // NO!
```

## Cross-Feature Communication

Since all code is in `commonMain`, it is physically possible to import anything. **Discipline is required.**

**Option 1: Move to Core**
If a model or logic is truly shared by multiple features, it belongs in `core`.
```kotlin
// core/domain/model/User.kt
```

**Option 2: Shared Interfaces in Core**
Define an interface in `core` that one feature implements and another uses.

**Option 3: Navigation with Arguments**
Pass primitive data or simple DTOs via navigation routes.
```kotlin
// Features communicate through navigation with results
navController.navigate(Screen.Detail.createRoute(id))
```
