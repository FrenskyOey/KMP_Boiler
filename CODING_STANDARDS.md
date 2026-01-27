# Coding Standards

## General
- **Kotlin Style Guide**: Follow official Kotlin coding conventions.
- **KDoc**: Document public interfaces and complex logic.

## Architecture
- **Clean Architecture**: Strictly separate Data, Domain, and UI.
- **Dependency Rule**: Domain should not depend on Data or UI. Data should not depend on UI.
- **Feature Isolation**: Features should not depend on other features directly.

## Naming Conventions
- **Classes/Interfaces**: PascalCase (e.g., `NewsRepository`).
- **Functions/Variables**: camelCase (e.g., `getNewsFeed`).
- **Composables**: PascalCase (e.g., `NewsScreen`), generally named as `Noun` or `NounAction`.
- **Resource Files**: snake_case (e.g., `ic_news_icon.xml`).

## Testing
- Write unit tests for UseCases and Repositories.
- Use `runTest` for coroutines.

## Git
- Commit messages should be descriptive.
- Use Conventional Commits (feat, fix, docs, chore, refactor).
