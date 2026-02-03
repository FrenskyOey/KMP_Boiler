---
description: Internal structure of source sets and layer separation guidelines
---

# Module Structure & Layer Separation

## Source Set Organization

In this Multiplatform project, code is organized by source sets within the `composeApp` module, rather than separate modules.

### Primary Source Sets:
- **commonMain**: Contains all shared business logic, UI, and feature implementations.
  - This is where `feature/` and `core/` packages reside.
- **androidMain**: Android-specific implementations (e.g., Activity, actual implementations).
- **iosMain**: iOS-specific implementations (e.g., KoinHelper, MainViewController).
- **desktopMain**: Desktop-specific implementations.

## Feature Structure Rules (`feature/`)

Features typically follow a component-based logical separation within `commonMain`.

### Feature Package Structure:

```kotlin
feature/[featureName]/
├── data/              # specific implementations
│   ├── api/           # Ktor API definitions
│   ├── dao/           # Room DAOs
│   ├── datasource/    # Data source implementations (Local/Remote)
│   ├── model/         # Data Transfer Objects (DTOs) & Entities
│   └── repository/    # Repository Implementation
├── domain/            # Business logic
│   ├── model/         # Domain models (pure Kotlin)
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Interactors/Use Cases
├── di/                # Feature DI Module
└── ui/                # Presentation
    ├── [screen]/      # Screen-specific package
    └── components/    # Feature-specific components
```

### Dependency Direction Within Feature
```
ui → domain ← data
```
- `ui/` depends on `domain/`
- `data/` depends on `domain/`
- `domain/` depends on NOTHING (except `core/domain`)

## Core Package Rules (`core/`)

The Core package follows a source-type separation to better organize shared infrastructure.

### Core Package Structure:

```kotlin
core/
├── data/
│   ├── remote/        # Network infrastructure
│   │   ├── base/      # Base network classes
│   │   └── util/      # Network utilities
│   ├── local/         # Local persistence infrastructure
│   │   ├── database/  # Database configuration
│   │   └── preferences/# DataStore configuration
│   └── mapper/        # Shared Mapper interfaces
├── domain/
│   ├── model/         # Shared domain models
│   ├── repository/    # Shared repository interfaces
│   ├── usecase/       # Shared business logic
│   └── config/        # App configuration & Constants
├── di/                # Core DI modules
└── util/              # Shared Utilities
```

### What Goes in Core:

**✅ Put in Core:**
- Result wrapper
- AppException types
- API error handling
- JSON serialization config
- Mapper interface
- PaginatedData model
- Database instance
- PreferencesManager interface
- Date formatting utilities
- Network configuration
- Logging utilities
- Theme
- **Shared Domain Models**
- **Shared Use Cases**
- **Shared Repository Interfaces**

**❌ Don't Put in Core:**
- Feature-specific models
- Feature-specific DTOs
- Feature-specific use cases
- Feature-specific repositories
- Feature business logic
