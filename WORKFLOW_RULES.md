# WORKFLOW RULES - KMP + CMP Project

## Purpose
This document defines the workflow for implementing features in a Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP) project using a structured, layer-by-layer approach with explicit planning phases.

---

## Core Principles

1. **Planning Before Implementation**: Always generate complete planning documents before writing any code
2. **Layer Isolation**: Implement one layer at a time, respecting dependency direction
3. **Incremental Progress**: Break implementation into small, reviewable pieces
4. **Test-Driven Approach**: Define test cases during planning, implement them alongside code
5. **Explicit Approval**: Wait for human approval before moving between phases

---

## Project Structure Context

### Module Organization
```
project-root/
├── composeApp/
├── src/
│   └── commonMain/
│       └── kotlin/
│           ├── core/
│           ├── feature/
│           │   └── [feature-name]/
│           │       ├── domain/
│           │       ├── data/
│           │       └── ui/
│           └── [package-name]/
│               └── feature/
│                   └── [feature-name]/
│                       ├── ui-plan.md
│                       ├── viewmodel/
│                       ├── state/
│                       ├── component/
│                       └── screen/
```

### Layer Dependencies
- **Domain Layer**: NO dependencies (pure Kotlin)
- **Data Layer**: Depends ONLY on Domain
- **UI Layer**: Depends ONLY on Domain (uses use cases)

---

## Development Phases

### PHASE 0: Requirement Clarification (NEW - MANDATORY)
**Trigger**: Human requests planning for any feature
**Objective**: Gather ALL necessary information before generating plans
**Reference**: See `REQUIREMENT_PROTOCOL.md` for complete details

#### What AI MUST Do:
1. **Read the feature request carefully**
2. **Identify missing information** by checking against required categories:
   - API/Backend details (endpoint, response structure, pagination)
   - Storage requirements (if mentioned - which fields, table schema)
   - UI/UX specifications (what to display, interactions)
   - Business logic (filtering, sorting, error handling)
3. **Ask specific, targeted questions** - do NOT guess or assume
4. **Wait for human to provide answers**
5. **Document all answers** for inclusion in plan files

#### CRITICAL RULES:
- ❌ **NEVER** proceed to Phase 1 without complete information
- ❌ **NEVER** make assumptions about API structure
- ❌ **NEVER** create fake/example JSON responses
- ❌ **NEVER** guess field names or data types
- ✅ **ALWAYS** ask for sample responses or clear descriptions
- ✅ **ALWAYS** clarify storage requirements before planning
- ✅ **ALWAYS** confirm UI field mappings

#### Example Interaction:
```
USER: "Build a news pagination feature with caching"

AI: "I'll help build this feature. Before generating plans, I need information:

📡 API Details:
1. What's the news API endpoint URL?
2. Can you provide a sample JSON response?
3. What pagination parameters? (page/limit, offset, cursor)

💾 Storage:
4. Which fields should be cached? (all or specific ones)
5. Table name for SQLDelight?
6. Cache duration?

🎨 UI:
7. What should each list item display?
8. What happens on item tap?
9. Pull-to-refresh needed?

Please provide this info so I can create accurate plans."

USER: [provides answers]

AI: "Perfect! Proceeding to generate plans..."
```

#### When to Ask Questions:
- API endpoint not specified → Ask for complete URL
- Response structure unknown → Ask for sample JSON
- "Store in database" without details → Ask which fields, table name
- "Display the data" without specifics → Ask which fields map to UI elements
- Error handling mentioned → Ask how to handle each error type
- Filtering/sorting mentioned → Ask for API parameters and UI approach

---

### PHASE 1: Feature Analysis & Planning
**Trigger**: Human requests planning for a feature (AFTER Phase 0 completion)
**Objective**: Generate three comprehensive plan documents
**Prerequisite**: All information from Phase 0 has been collected

#### What AI Should Do:
1. Review all answers collected in Phase 0
3. Generate three plan documents with documented requirements:
   - `domain-plan.md` → Store in `composeApp/src/commonMain/kotlin/feature/[feature-name]/domain/`
   - `data-plan.md` → Store in `composeApp/src/commonMain/kotlin/feature/[feature-name]/data/`
   - `ui-plan.md` → Store in `composeApp/src/commonMain/kotlin/[package]/feature/[feature-name]/`
3. Include "Requirements Clarification Summary" section in each plan with answers from Phase 0

#### What AI Should NOT Do:
- ❌ Generate any implementation code
- ❌ Proceed to Phase 2 without explicit approval
- ❌ Make assumptions about unclear requirements

#### Output Format:
After generating plans, AI should:
1. Present summary of what was planned
2. Highlight any assumptions made
3. Ask: "Plans generated. Please review and provide feedback. Type 'execute domain-plan' when ready to proceed."

---

### PHASE 2: Domain Layer Implementation
**Trigger**: Human explicitly says "execute domain-plan" or "implement domain layer"
**Objective**: Implement all items from `domain-plan.md`

#### Implementation Order:
1. **Models** (Data classes, sealed classes, enums)
   - Create domain models with proper documentation
   - Ensure immutability and proper null handling
2. **Repository Interfaces** (Abstractions)
   - Define contracts for data access
   - Include proper KDoc documentation
3. **Use Cases**
   - Implement business logic
   - Follow Single Responsibility Principle
   - One use case = one business operation
4. **Test Cases**
   - Unit tests for use cases
   - Test both success and failure scenarios
   - Mock repository dependencies

#### Incremental Implementation Rules:
- Implement ONE section at a time
- After each section, pause and show what was created
- Ask: "Section X completed. Review the code. Type 'continue' for next section or 'revise' to modify."
- Do NOT implement everything at once

#### File Placement:
```
shared/src/commonMain/kotlin/feature/[feature-name]/domain/
├── domain-plan.md (already exists)
├── model/
│   ├── [Feature]Data.kt
│   ├── [Feature]Error.kt
│   └── PaginationInfo.kt
├── repository/
│   └── [Feature]Repository.kt (interface)
└── usecase/
    ├── Get[Feature]UseCase.kt
    ├── Refresh[Feature]UseCase.kt
    └── LoadMore[Feature]UseCase.kt

shared/src/commonTest/kotlin/feature/[feature-name]/domain/
└── usecase/
    ├── Get[Feature]UseCaseTest.kt
    └── ...
```

#### Completion Criteria:
- [ ] All models created
- [ ] Repository interface defined
- [ ] All use cases implemented
- [ ] All test cases passing
- [ ] No compilation errors

---

### PHASE 3: Data Layer Implementation
**Trigger**: Human explicitly says "execute data-plan" or "implement data layer"
**Objective**: Implement all items from `data-plan.md`
**Prerequisite**: Domain layer MUST be complete

#### Implementation Order:
1. **Response Models & DTOs**
   - API response data classes
   - Mapping functions (DTO → Domain model)
2. **Data Source Interfaces**
   - Remote data source abstraction
   - Local data source abstraction (if caching)
3. **Remote Data Source Implementation**
   - Ktor API client implementation
   - Error handling and mapping
4. **Local Data Source Implementation** (if applicable)
   - SQLDelight or other local storage
   - CRUD operations
5. **Repository Implementation**
   - Implement domain repository interface
   - Combine remote + local data sources
   - Handle caching logic
6. **Test Cases**
   - Test data source implementations
   - Test repository with mocked sources
   - Test DTO mappings

#### Incremental Implementation Rules:
- Same as Phase 2: ONE section at a time
- Pause after each section for review
- Ensure each piece compiles before moving forward

#### File Placement:
```
shared/src/commonMain/kotlin/feature/[feature-name]/data/
├── data-plan.md (already exists)
├── model/
│   ├── [Feature]Response.kt
│   └── [Feature]Dto.kt
├── source/
│   ├── remote/
│   │   ├── [Feature]RemoteDataSource.kt (interface)
│   │   └── [Feature]RemoteDataSourceImpl.kt
│   └── local/
│       ├── [Feature]LocalDataSource.kt (interface)
│       └── [Feature]LocalDataSourceImpl.kt
├── repository/
│   └── [Feature]RepositoryImpl.kt
└── mapper/
    └── [Feature]Mapper.kt

shared/src/commonTest/kotlin/feature/[feature-name]/data/
├── source/
│   └── [Feature]RemoteDataSourceTest.kt
├── repository/
│   └── [Feature]RepositoryImplTest.kt
└── mapper/
    └── [Feature]MapperTest.kt
```

#### Completion Criteria:
- [ ] All response models and DTOs created
- [ ] Data sources implemented
- [ ] Repository implementation complete
- [ ] All mappers working correctly
- [ ] All test cases passing
- [ ] Integration with domain layer verified

---

### PHASE 4: UI Layer Implementation
**Trigger**: Human explicitly says "execute ui-plan" or "implement ui layer"
**Objective**: Implement all items from `ui-plan.md`
**Prerequisite**: Domain AND Data layers MUST be complete

#### Implementation Order:
1. **State Models**
   - UI State (data class for screen state)
   - UI Effect (sealed class for one-time events)
   - UI Event (sealed class for user actions)
2. **ViewModel Implementation**
   - State management with StateFlow
   - Effect handling with Channel/SharedFlow
   - Event processing
   - Use case integration
3. **Compose Components** (Reusable UI pieces)
   - List item components
   - Loading indicators
   - Error displays
4. **Screen Implementation**
   - Full screen composable
   - State observation
   - Event handling
   - Pagination logic (LazyColumn with load more)
5. **Navigation Integration**
   - Add screen to navigation graph
   - Handle arguments
6. **Dependency Injection Setup**
   - Register ViewModel in DI
   - Inject dependencies

#### Incremental Implementation Rules:
- Implement ONE component at a time
- Test each composable in preview if possible
- Verify ViewModel logic before UI integration

#### File Placement:
```
composeApp/src/commonMain/kotlin/[package]/feature/[feature-name]/
├── ui-plan.md (already exists)
├── state/
│   ├── [Feature]State.kt
│   ├── [Feature]Effect.kt
│   └── [Feature]Event.kt
├── viewmodel/
│   └── [Feature]ViewModel.kt
├── component/
│   ├── [Feature]ListItem.kt
│   ├── [Feature]LoadingIndicator.kt
│   └── [Feature]ErrorView.kt
└── screen/
    └── [Feature]Screen.kt
```

#### Completion Criteria:
- [ ] All state models defined
- [ ] ViewModel implemented and tested
- [ ] All UI components created
- [ ] Screen fully functional
- [ ] Navigation integrated
- [ ] DI configured
- [ ] App compiles and runs

---

## Plan Document Templates

### domain-plan.md Template
```markdown
# Domain Layer Plan - [Feature Name]

## Overview
- **Feature**: [Brief description]
- **Business Logic**: [What business rules this layer handles]

## 1. Models
### 1.1 Data Models
- **[ModelName]**: [Purpose and properties]
- **PaginationInfo**: [If pagination is needed]

### 1.2 Error Models
- **[Feature]Error**: [Error types to handle]

## 2. Repository Interface
### 2.1 Contract Definition
- `suspend fun get[Feature](page: Int): Result<List<[Model]>>`
- `suspend fun refresh(): Result<List<[Model]>>`
- [Other methods]

## 3. Use Cases
### 3.1 Get[Feature]UseCase
- **Responsibility**: [What it does]
- **Input**: [Parameters]
- **Output**: [Return type]

### 3.2 [OtherUseCase]
- [Same structure]

## 4. Test Cases
### 4.1 Use Case Tests
- ✓ Test successful data retrieval
- ✓ Test error handling
- ✓ Test pagination logic
- ✓ Test empty state
- [Additional test scenarios]

## 5. Implementation Checklist
- [ ] Create domain models
- [ ] Define repository interface
- [ ] Implement Get[Feature]UseCase
- [ ] Implement [OtherUseCase]
- [ ] Write use case tests
- [ ] Verify all tests pass
```

### data-plan.md Template
```markdown
# Data Layer Plan - [Feature Name]

## Overview
- **API Endpoint**: [URL and method]
- **Response Format**: [JSON structure overview]
- **Caching Strategy**: [Local storage approach]

## 1. Response Models & DTOs
### 1.1 API Response Model
```kotlin
// Expected structure
@Serializable
data class [Feature]Response(...)
```

### 1.2 DTO Models
- **[Feature]Dto**: [Intermediate representation]

### 1.3 Mapping Functions
- `[Feature]Dto.toDomain(): [DomainModel]`

## 2. Data Source Abstractions
### 2.1 Remote Data Source
```kotlin
interface [Feature]RemoteDataSource {
    suspend fun fetch[Feature](page: Int): [Feature]Response
}
```

### 2.2 Local Data Source (if applicable)
```kotlin
interface [Feature]LocalDataSource {
    suspend fun getAll(): List<[Feature]Dto>
    suspend fun insert(items: List<[Feature]Dto>)
    suspend fun clear()
}
```

## 3. Implementation Details
### 3.1 Remote Implementation
- **HTTP Client**: Ktor
- **Serialization**: kotlinx.serialization
- **Error Handling**: Map HTTP errors to domain errors

### 3.2 Local Implementation (if applicable)
- **Storage**: SQLDelight / DataStore
- **Schema**: [Table structure]

### 3.3 Repository Implementation
- Combine remote + local sources
- Implement caching logic
- Handle offline scenarios

## 4. Test Cases
### 4.1 Remote Data Source Tests
- ✓ Test successful API call
- ✓ Test network error handling
- ✓ Test JSON parsing
- ✓ Test pagination parameters

### 4.2 Repository Tests
- ✓ Test cache hit scenario
- ✓ Test cache miss scenario
- ✓ Test data synchronization
- ✓ Test error propagation

## 5. Implementation Checklist
- [ ] Create response models
- [ ] Create DTO models
- [ ] Implement mapping functions
- [ ] Create remote data source interface
- [ ] Implement remote data source
- [ ] Create local data source (if needed)
- [ ] Implement repository
- [ ] Write data source tests
- [ ] Write repository tests
- [ ] Verify all tests pass
```

### ui-plan.md Template
```markdown
# UI Layer Plan - [Feature Name]

## Overview
- **Screen Type**: [List, Detail, Form, etc.]
- **User Interactions**: [What users can do]
- **Navigation**: [How user reaches this screen]

## 1. State Management
### 1.1 UI State
```kotlin
data class [Feature]State(
    val items: List<[Model]> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = true
)
```

### 1.2 UI Effect (One-time events)
```kotlin
sealed interface [Feature]Effect {
    data class ShowToast(val message: String) : [Feature]Effect
    data class NavigateTo[Detail](val id: String) : [Feature]Effect
}
```

### 1.3 UI Event (User actions)
```kotlin
sealed interface [Feature]Event {
    object LoadInitial : [Feature]Event
    object LoadMore : [Feature]Event
    object Refresh : [Feature]Event
    data class ItemClicked(val id: String) : [Feature]Event
}
```

## 2. ViewModel
### 2.1 Dependencies
- Get[Feature]UseCase
- [OtherUseCases]

### 2.2 State Management
- Use StateFlow for state
- Use SharedFlow/Channel for effects
- Handle events in `onEvent(event: [Feature]Event)` function

### 2.3 Logic
- Initial load on ViewModel creation
- Pagination handling
- Error handling
- Pull-to-refresh

## 3. Compose Components
### 3.1 [Feature]ListItem
- Display individual item
- Handle click events
- Show loading state

### 3.2 LoadingIndicator
- Show during initial load
- Show at bottom for pagination

### 3.3 ErrorView
- Display error message
- Retry button

### 3.4 EmptyStateView
- Display when no data

## 4. Screen Implementation
### 4.1 [Feature]Screen Composable
- Observe state from ViewModel
- LazyColumn with pagination
- Pull-to-refresh
- Handle effects (navigation, toasts)

### 4.2 Pagination Logic
```kotlin
LazyColumn {
    items(state.items) { item ->
        [Feature]ListItem(item)
    }
    
    item {
        if (state.isLoadingMore) {
            LoadingIndicator()
        }
    }
}

// Load more when reaching end
LaunchedEffect(state.items.size) {
    if (should load more) {
        viewModel.onEvent(LoadMore)
    }
}
```

## 5. Navigation
### 5.1 Route Definition
- Define route constant
- Define arguments (if any)

### 5.2 Navigation Integration
- Add screen to NavHost
- Handle navigation from ViewModel effects

## 6. Dependency Injection
### 6.1 ViewModel Module
```kotlin
single { [Feature]ViewModel(get()) }
```

## 7. Test Cases (Optional for UI)
- ViewModel state transitions
- Effect emission
- Event handling

## 8. Implementation Checklist
- [ ] Define state models
- [ ] Define effect model
- [ ] Define event model
- [ ] Implement ViewModel
- [ ] Create ListItem component
- [ ] Create LoadingIndicator component
- [ ] Create ErrorView component
- [ ] Implement Screen composable
- [ ] Implement pagination logic
- [ ] Add pull-to-refresh
- [ ] Integrate navigation
- [ ] Setup DI
- [ ] Test on Android
- [ ] Test on iOS
```

---

## Communication Protocol

### When AI Should Ask Questions
- Requirements are ambiguous
- Multiple implementation approaches are valid
- Clarification needed on API structure
- UI/UX specifications are unclear

### When AI Should Pause
- After generating all three plan documents
- After completing each section of implementation
- When compilation errors occur
- When tests fail

### Expected Human Responses
- **"execute domain-plan"** → Start Phase 2
- **"execute data-plan"** → Start Phase 3
- **"execute ui-plan"** → Start Phase 4
- **"continue"** → Continue to next section within a phase
- **"revise [section]"** → Go back and modify a specific section
- **"skip [section]"** → Skip a section (with acknowledgment of risks)
- **"explain [concept]"** → Provide more context about a decision

---

## Error Handling Protocol

### If Implementation Fails
1. AI should identify the error
2. Suggest fix or ask for guidance
3. Wait for human decision
4. Do NOT proceed to next section until current section compiles

### If Tests Fail
1. Show test failure output
2. Analyze why test failed
3. Suggest fix
4. Rerun tests after fix
5. Do NOT proceed until tests pass

### If Requirements Change Mid-Phase
1. Stop current implementation
2. Update the relevant plan.md file
3. Ask human to review updated plan
4. Resume only after approval

---

## DI Integration Notes

### When to Setup DI
- **After Domain Layer**: Register use cases
- **After Data Layer**: Register repository implementations, data sources
- **After UI Layer**: Register ViewModels

### DI Module Organization
```kotlin
// In shared module
val domainModule = module {
    factory { Get[Feature]UseCase(get()) }
}

val dataModule = module {
    single<[Feature]Repository> { [Feature]RepositoryImpl(get(), get()) }
    single<[Feature]RemoteDataSource> { [Feature]RemoteDataSourceImpl(get()) }
}

// In composeApp module
val uiModule = module {
    viewModel { [Feature]ViewModel(get()) }
}
```

---

## Best Practices Reminders

### Code Quality
- Use meaningful variable names
- Add KDoc for public APIs
- Follow Kotlin coding conventions
- Use `Result<T>` for operations that can fail
- Prefer immutable data structures

### Testing
- Test business logic, not implementation details
- Use descriptive test names
- Follow Arrange-Act-Assert pattern
- Mock external dependencies

### Compose
- Use `remember` for expensive computations
- Extract reusable components
- Use `derivedStateOf` for computed state
- Handle side effects properly with `LaunchedEffect`

### Pagination
- Load first page on screen entry
- Load more when user scrolls near bottom
- Show loading indicators appropriately
- Handle errors gracefully
- Implement pull-to-refresh

---

## Checklist Before Starting a New Feature

Before beginning PHASE 0, ensure:
- [ ] Feature requirements are documented (at minimum a brief description)
- [ ] You have access to API documentation or can provide sample responses
- [ ] You have UI mockups or clear description of what should be displayed
- [ ] You understand what data needs to be stored locally (if any)
- [ ] You're ready to answer AI's clarifying questions
- [ ] Previous feature (if any) is fully complete

**Remember**: Phase 0 (Requirement Clarification) is MANDATORY before planning begins!

---

## Version
**Document Version**: 1.0
**Last Updated**: [Date]
**Applicable to**: KMP + CMP projects using Clean Architecture

---

## Notes for AI Assistant

- **CRITICAL**: Always run Phase 0 (Requirement Clarification) FIRST - see `REQUIREMENT_CLARIFICATION_PROTOCOL.md`
- **NEVER** skip the planning phase
- **NEVER** make assumptions about API structure, field names, or data types
- **ALWAYS** ask for clarification when information is missing or ambiguous
- **ALWAYS** wait for explicit human approval between phases
- **ALWAYS** store plan.md files in the correct feature directory
- **NEVER** implement all sections at once - be incremental
- **ALWAYS** ensure tests pass before moving forward
- **REMEMBER**: Domain → Data → UI (respect dependency direction)
- **BE PATIENT**: Human review is part of the workflow, not a bottleneck
- **DOCUMENT**: Include requirement clarification summary in all plan files

