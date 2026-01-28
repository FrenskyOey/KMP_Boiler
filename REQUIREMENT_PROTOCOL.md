# REQUIREMENT CLARIFICATION PROTOCOL

## Purpose
This document defines MANDATORY questions AI MUST ask before generating any plan or implementation. This prevents hallucination, wrong assumptions, and ensures accurate implementation aligned with actual requirements.

---

## Core Rule: ASK, DON'T ASSUME

**CRITICAL**: When information is missing or unclear, AI MUST:
1. ✅ **STOP** and ask specific questions
2. ✅ **WAIT** for human to provide answers
3. ✅ **DOCUMENT** answers in the plan files
4. ❌ **NEVER** make assumptions or guess
5. ❌ **NEVER** proceed with incomplete information
6. ❌ **NEVER** create fake/example data structures

---

## PHASE 1 GATE: Pre-Planning Checklist

Before generating ANY plan documents, AI MUST collect this information:

### 1. API/Backend Information (for Data Layer)

#### Required Questions:
```
🔴 MANDATORY - AI must ask these before proceeding:

1. API Endpoint Details:
   - What is the complete API URL/endpoint?
   - What is the HTTP method? (GET, POST, etc.)
   - Are there authentication requirements? (API key, Bearer token, etc.)
   - What are the required headers?

2. Request Parameters:
   - What query parameters are needed?
   - For pagination: What's the pagination strategy?
     * Offset-based? (page, limit, offset)
     * Cursor-based? (cursor, next_token)
     * Page-based? (page, per_page)
   - Are there filter parameters?
   - Are there sorting parameters?

3. Response Structure:
   - Can you provide a sample JSON response or describe the structure?
   - What is the root object? (is data wrapped in a parent object?)
   - What fields are in each item?
   - What are the data types of each field?
   - Are there nullable fields?
   - How is pagination info returned?
     * next_page, has_more, total_count?
     * Provide the exact field names

4. Error Response:
   - How does the API return errors?
   - What is the error response structure?
   - What error codes should we handle?

Example Clarification Dialog:
AI: "I need to understand the News API structure before planning. Please provide:
     1. The complete API endpoint URL
     2. A sample JSON response (paste here or describe the structure)
     3. Pagination parameters (e.g., page=1&limit=20)
     4. Any required headers or authentication"
```

---

### 2. Local Storage/Database Information (if caching is mentioned)

#### Required Questions:
```
🔴 MANDATORY - AI must ask these if local storage is involved:

1. Storage Strategy:
   - Should we cache this data locally?
   - Should data persist between app restarts?
   - What's the cache invalidation strategy?
     * Time-based? (cache for X hours)
     * Manual? (refresh button only)
     * Automatic? (on app launch)

2. Storage Technology:
   - Which storage solution should we use?
     * SQLDelight (for relational data)
     * DataStore (for key-value)
     * In-memory only (no persistence)
   - If unsure, AI should ask: "Do you have a preference for local storage technology?"

3. What to Store:
   - Which fields from the API response should be stored?
   - Should we store ALL fields or only specific ones?
   - Are there fields that should NOT be cached? (sensitive data, temporary tokens)
   - Should we store metadata? (timestamp, cache expiry, etc.)

4. Database Schema (if using SQLDelight):
   - What should the table be named?
   - What are the primary keys?
   - Are there any indexes needed for performance?
   - Should we store pagination info in DB?
   - How should we handle updates? (REPLACE, UPDATE, INSERT OR IGNORE)

5. Data Retention:
   - How much data should we keep? (last N items, all items, time-based)
   - Should old data be automatically deleted?
   - What triggers data cleanup?

Example Clarification Dialog:
AI: "You mentioned storing data locally. I need clarification:
     1. Should we cache all API response fields or only specific ones?
     2. Which fields are essential to cache? (title, content, image, date, etc.)
     3. What should the SQLDelight table be named? (e.g., 'news_articles')
     4. What's the primary key? (e.g., 'id' from API)
     5. Should cached data expire? If yes, after how long?"
```

---

### 3. UI/UX Requirements

#### Required Questions:
```
🔴 MANDATORY - AI must ask these for UI implementation:

1. Screen Layout:
   - What should be displayed in each list item?
   - Are there images? What's the image URL field name?
   - What's the primary text? (title, headline, name)
   - What's the secondary text? (description, date, author)
   - Should we show timestamps? In what format?

2. User Interactions:
   - What happens when user taps an item?
     * Navigate to detail screen?
     * Open URL in browser?
     * Show bottom sheet?
   - Should there be any action buttons on items?
   - Pull-to-refresh? (yes/no)
   - Infinite scroll or "Load More" button?

3. Loading States:
   - What should show during initial load?
   - What should show during "load more"?
   - Shimmer effect? Spinner? Progress bar?

4. Error Handling UI:
   - How should errors be displayed?
     * Toast messages?
     * Error view with retry button?
     * Snackbar?
   - What should error messages say?

5. Empty State:
   - What shows when there's no data?
   - Custom message or generic "No items found"?
   - Should there be an illustration?

6. Pagination Behavior:
   - When should "load more" trigger?
     * When scrolled to bottom?
     * When X items from bottom?
   - Show loading indicator at bottom of list?
   - Disable loading while request is in progress?

Example Clarification Dialog:
AI: "I need UI/UX details for the News list screen:
     1. For each list item, what should be displayed?
        - News image? (which field from API?)
        - Title? (which field?)
        - Description/snippet? (which field?)
        - Date? (format: 'Jan 27' or 'relative time'?)
     2. What happens when user taps a news item?
     3. Should the list support pull-to-refresh?
     4. How should we handle the 'load more' - automatic or button?"
```

---

### 4. Feature-Specific Business Logic

#### Required Questions:
```
🔴 MANDATORY - AI must ask these for use case implementation:

1. Business Rules:
   - Are there any filtering requirements?
     * Filter by category, date range, etc.?
   - Are there any sorting requirements?
     * Sort by date, popularity, etc.?
   - Default sort order?
   - Are there any data transformations needed?
     * Date formatting
     * Text truncation
     * Price calculations

2. Edge Cases:
   - What happens if API returns empty list?
   - What happens if network is unavailable?
     * Show cached data?
     * Show error?
   - What happens if user is offline but has cached data?
   - How to handle duplicate items from API?

3. Refresh Behavior:
   - Should refresh clear existing items or append?
   - Should refresh invalidate cache?
   - Should refresh reset pagination to page 1?

Example Clarification Dialog:
AI: "I need to understand the business logic:
     1. Should we filter news by any criteria? (category, date, source)
     2. What's the default sorting? (newest first, oldest first, popular)
     3. If network fails but we have cached data, should we show cache or error?
     4. When user pulls to refresh, should we clear the list or keep it visible?"
```

---

## Question Templates by Scenario

### Scenario 1: User Says "Build a [Feature] list with pagination"

**AI MUST ask:**
```
Before I generate the plans, I need this information:

📡 API Details:
1. What's the API endpoint URL?
2. Can you provide a sample JSON response?
3. What pagination parameters does the API use? (page/limit, offset, cursor)

💾 Storage (optional):
4. Should this data be cached locally?
5. If yes, which fields should be stored?

🎨 UI Requirements:
6. What should each list item display? (fields from the API)
7. What happens when user taps an item?
8. Should we have pull-to-refresh?

Once you provide this information, I'll generate domain-plan.md, data-plan.md, and ui-plan.md.
```

---

### Scenario 2: User Says "Store the data in database"

**AI MUST ask:**
```
To implement local storage correctly, I need:

1. Which database technology should I use?
   - SQLDelight (recommended for structured data)
   - DataStore (for simple key-value)
   
2. Which fields from the API should be stored in the database?
   - All fields?
   - Specific fields? (please list them)
   
3. What should the table be named?

4. What's the primary key?

5. Should we store pagination metadata? (page number, has_more flag)

6. Cache expiration strategy?
   - Keep data for X hours?
   - Manual refresh only?
   - No expiration?

I cannot proceed without this information to avoid creating an incorrect schema.
```

---

### Scenario 3: User Says "Add filtering by [category/date/etc]"

**AI MUST ask:**
```
To implement filtering, I need:

1. How does the API support filtering?
   - Query parameter? (e.g., ?category=tech)
   - POST body?
   - Different endpoint?

2. What are the available filter options?
   - Categories: [list them]
   - Date ranges: [specify options]

3. UI for filter selection:
   - Dropdown?
   - Chips/Tags?
   - Filter screen?

4. Should filters persist when user leaves the screen?

5. Default filter state? (all, none, specific value)

Without this info, I might implement filtering incorrectly.
```

---

### Scenario 4: User Says "Handle errors properly"

**AI MUST ask:**
```
To implement error handling that matches your requirements:

1. What error scenarios should we handle?
   - Network timeout
   - Server error (500, 503)
   - Unauthorized (401)
   - Not found (404)
   - Others?

2. How should each error be displayed to the user?
   - Toast message?
   - Error screen with retry?
   - Snackbar?

3. What should the error messages say?
   - Technical messages? ("HTTP 500")
   - User-friendly? ("Something went wrong")
   - Custom messages per error type?

4. Should there be a retry mechanism?
   - Automatic retry?
   - Manual retry button?

5. Should errors be logged/tracked? (Crashlytics, custom logging)
```

---

## HALT CONDITIONS

AI MUST STOP and refuse to proceed if:

### ❌ Missing Critical Information
```
"I cannot generate the data-plan.md without the API response structure. 
Making assumptions about field names and types will lead to incorrect implementation. 
Please provide a sample JSON response or describe the structure."
```

### ❌ Ambiguous Requirements
```
"You mentioned 'storing important fields' in the database, but I need to know 
exactly which fields to store. Please specify which fields from the API response 
should be cached locally."
```

### ❌ Contradictory Instructions
```
"You mentioned both 'cache all data locally' and 'only store user favorites'. 
These requirements contradict each other. Please clarify which approach to take."
```

### ❌ Unclear Business Logic
```
"I'm unclear on the refresh behavior. Should refreshing:
 A) Clear the list and load page 1, or
 B) Prepend new items to existing list?
Please specify so I implement the correct behavior."
```

---

## Documentation in Plans

When requirements are clarified, AI MUST document them in the plan files:

### Example in domain-plan.md:
```markdown
## Requirements Clarification Summary

**Feature**: News Article Pagination
**API Endpoint**: https://api.example.com/v1/news
**Pagination Strategy**: Offset-based (page, limit)
**Caching**: Yes, cache all fields in SQLDelight
**Cache Duration**: 1 hour

### API Response Structure (provided by user):
{
  "data": [
    {
      "id": "string",
      "title": "string",
      "description": "string",
      "imageUrl": "string",
      "publishedAt": "ISO8601 date string",
      "source": "string"
    }
  ],
  "pagination": {
    "currentPage": 1,
    "totalPages": 10,
    "hasMore": true
  }
}

### Storage Requirements (confirmed):
- Table name: news_articles
- Primary key: id
- Store all fields
- Add timestamp for cache expiry
```

---

## Workflow Integration

This protocol integrates with `WORKFLOW_RULES.md` as follows:

### Before PHASE 1 (Planning):
1. AI reads feature request
2. **AI RUNS THIS PROTOCOL** - asks all relevant questions
3. Human provides answers
4. AI documents answers
5. THEN AI proceeds to generate plan files

### During PHASE 2-4 (Implementation):
- If AI encounters ambiguity during implementation → STOP and ask
- If requirements need adjustment → Update plan.md and ask for confirmation
- If new questions arise → Ask before proceeding

---

## Example Full Interaction

```
👤 USER: "Build a news pagination feature with local caching"

🤖 AI: "I'll help you build this feature. Before generating the plans, I need to gather 
some information to ensure accurate implementation:

📡 API Configuration:
1. What's your news API endpoint URL?
2. Can you provide a sample JSON response? (paste here or describe the structure)
3. What pagination parameters does your API use? 
   - Example: ?page=1&limit=20 or ?offset=0&limit=20
4. Does the API require authentication? (API key, bearer token, etc.)

💾 Local Storage:
5. Which fields from the news API should be cached?
   - All fields or specific ones? (id, title, image, etc.)
6. What should the SQLDelight table be named? (e.g., 'news_articles')
7. How long should cached data be valid? (1 hour, 24 hours, manual refresh only)

🎨 UI Details:
8. For each news item in the list, what should be displayed?
   - News image? (which API field?)
   - Title? (which API field?)
   - Description/snippet? (which API field?)
   - Date? (how should it be formatted?)
9. What happens when user taps a news article?
10. Should we have pull-to-refresh?

Please provide this information and I'll generate comprehensive plans for domain, data, 
and UI layers."

👤 USER: [provides detailed answers]

🤖 AI: "Perfect! I now have all the information needed. Let me generate:
- domain-plan.md
- data-plan.md  
- ui-plan.md

All plans will include the requirement clarification summary for reference."

[AI proceeds to PHASE 1 with accurate information]
```

---

## Checklist for AI

Before generating any plan, confirm:
- [ ] API endpoint URL is provided (not guessed)
- [ ] API response structure is known (sample provided or described)
- [ ] Pagination strategy is clear (offset/cursor/page-based)
- [ ] Storage requirements are specified (if caching mentioned)
- [ ] Database schema details provided (if using DB)
- [ ] UI fields mapped to API response fields
- [ ] User interaction behaviors specified
- [ ] Error handling approach defined
- [ ] All clarifications documented in plan files

---

## Benefits of This Protocol

1. ✅ **Prevents hallucination** - No fake data structures
2. ✅ **Ensures accuracy** - Implementation matches actual API
3. ✅ **Saves time** - Correct first time, no rework
4. ✅ **Documents requirements** - Plans serve as specification
5. ✅ **Reduces debugging** - Fewer mismatches between plan and reality
6. ✅ **Professional workflow** - Mimics real-world development process

---

## Version
**Document Version**: 1.0
**Last Updated**: [Date]
**Works with**: WORKFLOW_RULES.md v1.0

---

## Critical Reminder for AI

**"When in doubt, ASK. Never guess. Your job is to implement accurately, not creatively 
assume requirements. Missing information = STOP and request clarification."**
