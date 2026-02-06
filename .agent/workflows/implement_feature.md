---
description: Detailed 4-phase protocol for implementing features (Requirement Clarification -> Planning -> Execution)
---

# Feature Implementation Protocol

**Trigger**: User requests a new feature or complex change.

## // turbo-all
This workflow uses `// turbo-all` to auto-execute command steps, but requires manual pauses for user interactions in each phase.

## Phase 0: Requirement Clarification (MANDATORY)

**Objective**: Gather ALL necessary information before generating plans.

1. **Activate Skill**:
    - Use the `clarify_requirements` skill to analyze the request and ask mandatory questions.
    - **Do NOT** proceed until the skill's checklist is satisfied.

    > [!IMPORTANT]
    > Trust the skill to guide the clarification process. It covers API, Storage, and UI requirements.

## Phase 1: Planning

**Objective**: Generate comprehensive plan documents.

1. **Create Plan Files**:
    - Create `composeApp/src/commonMain/kotlin/feature/[name]/domain/domain-plan.md`
    - Create `composeApp/src/commonMain/kotlin/feature/[name]/data/data-plan.md`
    - Create `composeApp/src/commonMain/kotlin/feature/[name]/ui/ui-plan.md`

2. **Populate Plans**:
    - Use the templates defined in `WORKFLOW_RULES.md`.
    - Include a "Requirements Clarification Summary" in each plan.
    - **Impact Analysis**: List existing tests/components (Domain, Data, ViewModel) that will be broken by this change and the strategy to fix them (Update vs Delete).

3. **Request Review**:
    - Ask the user to review the plans.
    - **WAIT** for explicit approval (e.g., "execute domain-plan").

## Phase 2: Domain Layer Implementation

**Trigger**: User approves `domain-plan.md`.

1. **Implement Models**:
    - Create data classes, sealed classes in `domain/model/`.
2. **Implement Repository Interface**:
    - Define interface in `domain/repository/`.
3. **Activate Skill (TDD)**:
    - Use the `tdd_implementation` skill.
    - **Instruction**: Write tests for Use Cases first, then implement.
4. **Implement Use Cases**:
    - Create use cases in `domain/usecase/`.
4. **Implement Tests**:
    - Write unit tests in `commonTest`.
5. **Verify**:
    - Run tests to ensure domain logic is sound.

## Phase 3: Data Layer Implementation

**Trigger**: User approves `data-plan.md` (or after Domain is done).

**Pre-requisite**: Activate `data_implementation` skill.

1.  **Define Structure**:
    - Create `data/model/` structure (`entity`, `request`, `response`, `mapper`).
    - Define `[Feature]ApiService` interface in `data/api/`.
    - Define `[Feature]DataSource` interface in `data/datasource/`.

2.  **Activate Skill (TDD)**:
    - Use the `tdd_implementation` skill.
    - **Instruction**: Write tests for Repositories and Data Sources first, then implement.

3.  **Implement Components (Standardized)**:
    - **ApiService**: Implement Ktor logic in `data/api/`.
    - **Data Sources**: Implement `Remote` (Throw Errors) and `Local` (DataStore/DB) in `data/datasource/`.
    - **Repository**: Implement interface in `data/repository/` (Return Result).

4.  **DI Registration**:
    - Update `di/[Feature]Module.kt` to bind Service, DataSources, and Repository.

5.  **Verify**:
    - Run tests for data sources and repository.

## Phase 4: UI Layer Implementation

### Phase 4.1: UI Design Specification
**Trigger**: User approves `ui-overview-plan.md` (or after Data is done).

**Objective**: Create detailed UI specification before any code is written.

1. **UI Requirements Gathering Session**:
    - **PAUSE & ASK USER**:
        - What screens/views are needed? (e.g., List Screen, Detail Screen, Edit Form)
        - What's the primary user flow/journey?
        - Any reference designs? (sketches, wireframes, apps to mimic)
        - Design system requirements? (brand colors, spacing rules, typography)
        - Device considerations? (phone only, tablet layouts, desktop?)

2. **Create Component Inventory**:
    - List ALL UI components needed for this feature
    - Categorize each as:
        - **Reusable** (can be used in multiple places)
        - **Screen-specific** (only for this feature)
    - Identify:
        - ✅ Components that already exist (reuse)
        - 🆕 Components that need to be created

3. **Create Detailed UI Spec Document**:
    - Create `composeApp/src/commonMain/kotlin/feature/[name]/ui/ui-spec.md`
    - Include:
        - **Screen Layouts**:
            - ASCII wireframes or detailed descriptions
            - Layout structure (LazyColumn, Grid, etc.)
        - **Component Breakdown**:
            - Each component with:
                - Purpose
                - Props/Parameters
                - States (normal, loading, error, disabled)
                - User interactions (click, swipe, etc.)
                - Visual appearance description
        - **State Management Map**:
            - Which UI state classes are needed
            - Which events trigger what
            - Which ViewModels handle what screens
        - **Navigation Flow**:
            - How screens connect
            - Navigation arguments
            - Back stack behavior
        - **Edge Cases**:
            - Loading states
            - Empty states
            - Error states
            - Offline behavior

4. **Request Review**:
    - Present the `ui-spec.md` to user
    - **WAIT** for explicit approval or refinement requests
    - Allow iteration on design before coding

### Phase 4.2: State & ViewModel Implementation
**Trigger**: User approves `ui-spec.md`.

**Objective**: Build the state management foundation.

1. **Implement State Classes**:
    - Create `ui/state/[FeatureName]State.kt` (data class for UI state)
    - Create `ui/state/[FeatureName]Event.kt` (sealed class for user actions)
    - Create `ui/state/[FeatureName]Effect.kt` (sealed class for one-time effects)

2. **Implement ViewModel**:
    - Create `ui/viewmodel/[FeatureName]ViewModel.kt`
    - Implement state management logic
    - Wire up use cases from domain layer
    - Handle event processing
    - Manage side effects

3. **Verify**:
    - Review ViewModel logic
    - Ensure all states/events from spec are covered

4. **Request Approval**:
    - Show user the ViewModel structure
    - **WAIT** for approval to proceed to components

### Phase 4.3: Component Implementation (Iterative)
**Trigger**: User approves ViewModel implementation.

**Objective**: Build UI components one-by-one or in logical groups.

**For EACH component** (or logical group of related components):

1. **Announce Component**:
    - Inform user: "Now implementing: [ComponentName]"
    - Show component spec from `ui-spec.md`

2. **Implement Component**:
    - Create file in `ui/components/[ComponentName].kt`
    - Include:
        - Component Composable function
        - Preview functions (for different states)
        - Internal helper functions if needed
    - Follow design spec exactly

3. **Show Preview Code**:
    - Display the component code
    - Explain key decisions made

4. **Request Approval**:
    - **WAIT** for user to:
        - ✅ Approve and move to next component
        - 🔄 Request changes to current component
        - 📋 Provide additional design details

5. **Iterate**:
    - Repeat steps 1-4 for each component in the inventory

**Component Priority Order**:
- Start with foundational/reusable components (buttons, cards)
- Then screen-specific components
- Finally complex composed components

### Phase 4.4: Screen Composition
**Trigger**: All components are approved.

**Objective**: Assemble components into complete screens.

1. **Implement Screen Composables**:
    - Create `ui/screen/[ScreenName]Screen.kt` for each screen
    - Compose approved components together
    - Wire to ViewModel (state, events)
    - Handle navigation callbacks

2. **Implement Screen-Level Logic**:
    - Loading indicators
    - Error handling UI
    - Empty state displays
    - Pull-to-refresh (if applicable)
    - Dialogs/Bottomsheets

3. **Verify Screens**:
    - Review each screen implementation
    - Ensure matches `ui-spec.md`

4. **Request Approval**:
    - **WAIT** for user to review screen compositions
    - Allow for layout adjustments

### Phase 4.5: Navigation & Integration
**Trigger**: All screens are approved.

**Objective**: Wire the feature into the app.

1. **Setup Dependency Injection**:
    - Create `di/[FeatureName]Module.kt` (if not exists)
    - Register ViewModel, UseCases, Repositories in Koin
    - Add module to main Koin configuration

2. **Setup Navigation**:
    - Add routes to navigation graph (e.g., in `navigation/NavGraph.kt`)
    - Add navigation arguments if needed
    - Setup deep links (if applicable)
    - Wire navigation from other parts of app (if needed)

3. **Integration Points**:
    - Update any existing screens that link to this feature
    - Update main menu/navigation drawer/bottom bar (if needed)

4. **Verify Build**:
    - Compile the project
    - Check for errors
    - Verify no broken dependencies

5. **Request Testing**:
    - Ask user to test the feature
    - **WAIT** for feedback

## Completion
- **Final Review**: User tests the complete feature flow
- **Bug Fixes**: Address any issues found during testing
- **Cleanup**:
    - Remove any unused code
    - Update documentation
    - Mark the feature task as complete
