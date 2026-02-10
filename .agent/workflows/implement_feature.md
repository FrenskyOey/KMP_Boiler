---
description: Detailed 4-phase protocol for implementing features (Requirement Clarification → Planning → Execution)
---

# Feature Implementation Protocol

**Trigger**: User requests a new feature or complex change.

## // turbo-all

---

## Phase 0: Requirement Clarification (MANDATORY)

1. **Activate `clarify_requirements` skill**
2. Ask mandatory questions
3. **WAIT** for user responses

---

## Phase 1: Planning

1. **Create task.md** in artifacts directory
   - Break down feature into checklist items
   - Organize by layer (Domain → Data → UI → Integration)

2. **Create plan files**:
   - `feature/[name]/domain/domain-plan.md`
   - `feature/[name]/data/data-plan.md`
   - `feature/[name]/ui/ui-plan.md`

3. **Request Review** - WAIT for approval

---

## Phase 1.5: Address User Concerns (If Raised)

**Trigger**: User identifies issues with the plan.

1. **Create `improvements_summary.md`**
   - For each concern: Problem → Solution → Code Examples → Impact

2. **Common concerns**: Configuration changes, performance, security, edge cases

3. **Request re-approval** - WAIT for approval

4. **Update plans** if needed

---

## Phase 2: Domain Layer

**Trigger**: User approves domain-plan.md

1. Implement models (`domain/model/`)
2. Implement repository interface (`domain/repository/`)

3. **TDD (MANDATORY)**:
   - Activate `tdd_implementation` skill
   - Write FAILING test for UseCase
   - Show test failure
   - Implement UseCase
   - Show test passing

4. **Verify**: Run all domain tests, update task.md

---

## Phase 3: Data Layer

**Trigger**: User approves data-plan.md

**Pre-requisite**: Activate `data_implementation` skill

1. Define structure:
   - `data/model/` (entity, request, response, mapper)
   - `data/api/[Feature]ApiService`
   - `data/datasource/[Feature]DataSource`

2. **TDD (MANDATORY)**:
   - Write FAILING test for Repository
   - Show test failure
   - Implement Repository + DataSources
   - Show tests passing

3. **DI Registration**: Update `di/[Feature]Module.kt`

4. **Verify**: Run all data tests, update task.md

---

## Phase 4: UI Layer

> [!CAUTION]
> **FORBIDDEN**: Modifying data/domain logic without user confirmation.
> **SCOPE**: Only create/edit files in ui/presentation layer.

### Phase 4.1: UI Design Specification

1. **Verify Domain Readiness** - Check all UseCases exist
2. **UI Requirements Session** - Ask about screens, flows, design system
3. **Create Component Inventory** - List all needed components
4. **Create `ui-spec.md`** with wireframes, component breakdown, state map
5. **Request Review** - WAIT for approval

### Phase 4.2: State & ViewModel

1. Implement state classes (`ui/state/`)
   - `[Feature]State.kt` (UI state)
   - `[Feature]Event.kt` (user actions)
   - `[Feature]Effect.kt` (one-time effects)

2. Implement ViewModel (`ui/viewmodel/`)
   - Wire up use cases
   - Handle events
   - Manage effects

3. **Request Approval** - WAIT

### Phase 4.3: Component Implementation (Iterative)

**Pre-requisite**: Activate `ui_implementation` skill

**For EACH component**:

1. **Announce**: "Now implementing: [ComponentName]"

2. **Implement**: Create `ui/components/[ComponentName].kt`

3. **Validate (MANDATORY)**:
   - Activate `ui_validation` skill
   - Scan for hardcoded values
   - Auto-fix violations
   - State "UI Validation: [Pass/Fail]"

4. **Request Approval** - WAIT

5. **Update task.md**

### Phase 4.4: Screen Composition

1. Create screen composables (`ui/screen/`)
2. Wire to ViewModel
3. Handle loading/error/empty states
4. **Request Approval** - WAIT

### Phase 4.5: Navigation & Integration

1. **Setup DI**:
   - Create/update `di/[Feature]Module.kt`
   - Register in ALL platforms:
     - Android: `androidMain/.../MyApp.kt`
     - iOS: `iosMain/.../KoinHelper.kt`
     - Desktop: `desktopMain/.../Main.kt`

2. **Setup Navigation**:
   - Add routes to navigation graph
   - Add navigation arguments
   - Setup deep links

3. **Verify Build**: Compile project

4. **Request Testing** - WAIT for feedback

---

## Completion

- User tests feature
- Fix bugs
- Cleanup unused code
- Mark task complete

---

## Quick Reference

**Key Skills**:
- `clarify_requirements` - Phase 0
- `tdd_implementation` - Phase 2 & 3
- `data_implementation` - Phase 3
- `ui_implementation` - Phase 4.3
- `ui_validation` - Phase 4.3

**Key Artifacts**:
- `task.md` - Progress tracking
- `improvements_summary.md` - Address concerns
- `domain-plan.md` - Domain layer spec
- `data-plan.md` - Data layer spec
- `ui-spec.md` - UI design spec

**Mandatory Checkpoints**:
- ✅ TDD for Domain & Data layers
- ✅ UI validation for all components
- ✅ DI registration on all platforms
- ✅ User approval at each phase
