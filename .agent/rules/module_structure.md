---
description: Internal structure of modules and layer separation guidelines
---

# Module Structure & Layer Separation

## Layer Separation Within Features

### Each Feature Must Have:

1. **data/** - Data layer
   - `remote/` - API services, DTOs
   - `local/` - DAOs, Entities  
   - `repository/` - Repository implementations
   - `mapper/` - DTO ↔ Domain mappers

2. **domain/** - Business logic
   - `model/` - Domain models (pure Kotlin)
   - `repository/` - Repository interfaces
   - `usecase/` - Business logic

3. **di/** - Dependency injection
   - One Koin module per feature

### Dependency Direction Within Feature
```
data → domain ← (nothing)
```
- `data/` depends on `domain/`
- `domain/` depends on NOTHING (except core)

## Core Module Rules

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

**❌ Don't Put in Core:**
- Feature-specific models
- Feature-specific DTOs
- Feature-specific use cases
- Feature-specific repositories
- Feature business logic

### Core Module Structure

```kotlin
core/
├── data/
│   ├── remote/
│   │   ├── util/              # API utilities
│   │   └── base/              # Base API classes
│   ├── local/
│   │   ├── database/          # Room database
│   │   └── preferences/       # DataStore
│   └── mapper/                # Mapper interface
├── domain/
│   ├── model/                 # Shared domain models
│   └── config/                # App configuration
├── di/                        # Core DI modules
└── util/                      # Utilities
```
