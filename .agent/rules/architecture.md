---
description: High-level architectural rules and cross-feature boundaries
---

# Feature-First Architecture Rules

## Project Context
This is a Kotlin Multiplatform (KMP) project with **Feature-First Modular Architecture**. Each feature is self-contained with its own data, domain, and DI layers.

## Feature Structure
```
shared/src/commonMain/kotlin/
├── core/               # Shared utilities ONLY
│   ├── data/
│   ├── domain/
│   ├── di/
│   └── util/
└── feature/            # Feature modules
    ├── news/
    │   ├── data/
    │   ├── domain/
    │   ├── di/
    └── settings/
        ├── data/
        ├── domain/
        └── di/
```

## CRITICAL Dependency Rules

**✅ ALLOWED:**
- Features can depend on `core/`
- Features can depend on external libraries
- UI layer can depend on any feature

**❌ FORBIDDEN:**
- Features CANNOT depend on other features
- Features CANNOT import from other features
- Features CANNOT share code directly

**Example:**
```kotlin
// ✅ CORRECT - Feature depends on core
import com.example.core.domain.model.Result
import com.example.feature.news.domain.model.NewsFeed

// ❌ WRONG - Feature depends on another feature
import com.example.feature.settings.domain.model.AppSettings // NO!
```

## When Features Need to Communicate

If features need to share data or communicate:

**Option 1: Move to Core**
```kotlin
// If multiple features need it, move to core/domain/model/
core/domain/model/User.kt
```

**Option 2: Use Events/Messages**
```kotlin
// Create event bus in core
core/domain/event/AppEvent.kt
```

**Option 3: Through UI/Navigation**
```kotlin
// Features communicate through navigation with results
navController.previousBackStackEntry?.savedStateHandle?.set("result", data)
```
