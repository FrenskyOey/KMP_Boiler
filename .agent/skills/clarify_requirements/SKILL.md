---
name: clarify_requirements
description: Ask mandatory questions to clarify requirements before planning a feature.
---

# Requirement Clarification Skill

## Purpose
This skill ensures that the agent gathers ALL necessary information before generating any plan or implementation for a new feature. It prevents assumptions and ensures accuracy.

## When to Use
- When the user asks to implement a new feature.
- When the user requests a complex change that requires planning.
- Triggered automatically by the `implement_feature` workflow.

## Instructions

### 1. Analyze the Request
Read the user's request and identify missing information in these categories:
- **API/Backend**: Endpoint, response structure, pagination, authentication.
- **Storage**: Caching strategy, database schema, retention.
- **UI/UX**: Layout, interactions, loading states, error handling.
- **Business Logic**: Filtering, sorting, data transformations.

### 2. Formulate Questions (MANDATORY)
You MUST ask these questions if the information is missing.

**API Details:**
- What is the complete API URL/endpoint?
- Can you provide a sample JSON response or describe the structure?
- What are the pagination parameters? (page, limit, offset, etc.)

**Storage (if caching is needed):**
- Should we cache this data? Which fields?
- What table name should we use?
- What is the cache expiration strategy?

**UI/UX:**
- What should be displayed in the list items?
- What happens on item click?
- Pull-to-refresh? Infinite scroll?

### 3. Interaction Loop
1.  **Ask**: Present the questions to the user.
2.  **Wait**: Stop and wait for the user's response.
3.  **Document**: Once answered, summarize the requirements.

## Constraints
- ❌ **NEVER** make assumptions about API structure or fields.
- ❌ **NEVER** proceed to planning without these answers.
- ❌ **NEVER** create fake example responses; ask the user for them.
