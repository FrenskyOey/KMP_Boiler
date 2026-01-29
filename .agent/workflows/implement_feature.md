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
    - Create `shared/src/commonMain/kotlin/feature/[name]/domain/domain-plan.md`
    - Create `shared/src/commonMain/kotlin/feature/[name]/data/data-plan.md`
    - Create `composeApp/src/commonMain/kotlin/[package]/feature/[name]/ui-plan.md`

2. **Populate Plans**:
    - Use the templates defined in `WORKFLOW_RULES.md`.
    - Include a "Requirements Clarification Summary" in each plan.

3. **Request Review**:
    - Ask the user to review the plans.
    - **WAIT** for explicit approval (e.g., "execute domain-plan").

## Phase 2: Domain Layer Implementation

**Trigger**: User approves `domain-plan.md`.

1. **Implement Models**:
    - Create data classes, sealed classes in `domain/model/`.
2. **Implement Repository Interface**:
    - Define interface in `domain/repository/`.
3. **Implement Use Cases**:
    - Create use cases in `domain/usecase/`.
4. **Implement Tests**:
    - Write unit tests in `commonTest`.
5. **Verify**:
    - Run tests to ensure domain logic is sound.

## Phase 3: Data Layer Implementation

**Trigger**: User approves `data-plan.md` (or after Domain is done).

1. **Implement Response/DTOs**:
    - Create models in `data/model/` matching API JSON.
2. **Implement Mappers**:
    - Create mapping functions (DTO -> Domain).
3. **Implement Data Sources**:
    - Create `RemoteDataSource` (Ktor) and `LocalDataSource` (Room/DataStore) in `data/source/`.
4. **Implement Repository**:
    - Implement the interface in `data/repository/`.
5. **Verify**:
    - Run tests for data sources and repository.

## Phase 4: UI Layer Implementation

**Trigger**: User approves `ui-plan.md` (or after Data is done).

1. **Implement State/Events**:
    - Create `State`, `Event`, `Effect` classes in `ui/state/`.
2. **Implement ViewModel**:
    - Create ViewModel in `ui/viewmodel/`.
3. **Implement Components**:
    - Create reusable Composables in `ui/components/`.
4. **Implement Screen**:
    - Create main Screen Composable in `ui/screen/`.
5. **Setup Navigation & DI**:
    - Register feature module in Koin.
    - Add to Navigation graph.
6. **Verify**:
    - Compile and check for errors.

## Completion

- **Final Review**: Ask user to test the feature.
- **Cleanup**: Mark the feature task as complete.
