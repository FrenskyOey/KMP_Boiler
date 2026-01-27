# Architecture Documentation

## Feature-First Modular Architecture

This project follows a **Feature-First** modular architecture to ensure scalability, maintainability, and team collaboration.

### Structure Overview
```
shared/
└── feature/
    ├── news/
    │   ├── data/
    │   ├── domain/
    │   └── ui/ (UI Logic / ViewModels in CommonMain)
    └── settings/
        ├── data/
        ├── domain/
        └── ui/
```

### Key Principles

1.  **Self-Contained Features**: Each feature (e.g., `news`, `settings`) is self-contained with its own Data, Domain, and DI layers.
2.  **No Inter-Feature Dependencies**: Features do NOT depend on each other. If Feature A needs something from Feature B, they should communicate via the `core` module or a shared interface/event bus defined in `core` (or the app module facilitates it).
3.  **Core Module**: Contains shared utilities, base classes, global configurations (Network, Database), and common UI components.
4.  **Shared Logic**: All business logic and data handling reside in the KMP `shared` module (`commonMain`).
5.  **Multiplatform UI**: UI logic (ViewModels) and components are shared where possible (using Compose Multiplatform).
6.  **Platform Isolation**: Code in `commonMain` MUST be free of platform-specific imports (e.g., `android.*`, `UIKit`). Use `expect`/`actual` interfaces to abstract platform-specific behavior.

### Module Responsibilities

- **composeApp**: The entry point for the Android application. It sets up the dependency graph (Koin), handles navigation (if at app level), and delegates to feature screens.
- **shared**: The heart of the application.
    - `core`: Infrastructure (Network, DB, Preferences), Base Models (Result, Exception), Common UI Theme/Components.
    - `feature/[name]`: Specific feature implementation.
        - `domain`: Pure Kotlin business logic, Use Cases, Repository Interfaces, Models.
        - `data`: Repository Implementations, API Services, DTOs, Mappers.
        - `di`: Koin module for the feature.

### Technology Stack
- **Kotlin Multiplatform**: Shared logic and UI.
- **Koin**: Dependency Injection.
- **Ktor**: Networking.
- **Room**: Local Database.
- **DataStore**: Preferences.
- **Compose Multiplatform**: UI.
- **Coil**: Image Loading.
- **Navigation Compose**: Navigation.

### Flavor Configuration
- **Staging**: `https://staging-api.example.com/v1/`
- **Production**: `https://api.example.com/v1/`
