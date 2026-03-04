# AI Usage Disclosure

## AI Tools Used

- **Gemini Pro** (via Android Studio integration) — Used for in-editor code suggestions and autocomplete while writing Kotlin code.
- **Claude Opus** (via Antigravity) — Used for brainstorming architecture decisions, debugging specific issues, and refining code quality.

---

## Where AI Was Used

| Area | How AI Assisted |
|---|---|
| **Pagination logic** (`FetchNewsDetailsViewmodel.kt`) | AI helped implement the dynamic pagination flow — managing `currentPage`, `isLastPage`, and `isFetching` flags in the ViewModel to coordinate scroll-based loading. |
| **ViewModel state management** (`FetchNewsDetailsViewmodel.kt`) | AI suggested how to structure the initial load vs. pagination calls, and how to reset state properly when the user performs a new search. |
| **Search functionality** (`NewsFeedActivity.kt`) | AI assisted with implementing the search debounce using coroutines and structuring the `TextWatcher` logic for a smoother search experience. |
| **Code optimization & cleanup** (across multiple files) | Used AI to shorten verbose syntax, suggest more descriptive variable/function names, and remove unnecessary boilerplate code. |

> **Note on UI Design**: Approximately **95% of the UI was designed and built by me independently**. I used [Google Stitch](https://stitch.withgoogle.com/) as a reference tool to explore layout ideas and get visual inspiration, but all XML layouts, styling, color choices, and component structuring were implemented manually by me.

---

## What AI Output Was Accepted vs. Modified

### Accepted (with minor tweaks)
- **`Resource` sealed class** — The Loading/Success/Error pattern was a standard AI suggestion that I adopted since it is a well-established pattern for state management in Android.
- **Clean Architecture structure** — AI initially suggested a simpler MVVM setup with everything in a flat package structure. I pushed for a layered Clean Architecture (data → domain → presentation) with separate models, mappers, and use cases because it enforces proper separation of concerns. Even though it adds more files for a small project, it makes the codebase genuinely scalable — adding a new feature means touching only the relevant layer without side effects.
- **Lottie animation for empty state** — AI suggested using a simple static `TextView` saying "No data found" for the empty state. I replaced it with a Lottie animation (`no_data_lottie.json`) paired with styled text, which creates a much more polished and professional feel. Small touches like this significantly improve the user's first impression.

### Modified
- **Pagination in ViewModel** — AI initially loaded the next page of data immediately when triggered. I added a debounce/guard mechanism (`isFetching` flag) and tuned the scroll threshold to 5 items to provide a smoother user experience, rather than firing requests too aggressively.
- **Variable and function naming** — AI-generated names were sometimes too generic (e.g., `getData`, `result`). I renamed them to be more domain-specific (e.g., `fetchNews`, `newsResponse`, `allArticles`) so the code reads more naturally.

---

## What AI Suggestions Were Rejected and Why

1. **Room Database for offline caching** — AI suggested using Room to persist articles locally. I rejected this because for a single-screen news feed with one cached response, Room introduces unnecessary complexity (entity definitions, DAO interfaces, database class). `SharedPreferences` with Gson serialization is simpler, faster to implement, and fully sufficient for caching the last successful API response.

2. **Single ProgressBar for all loading states** — AI suggested reusing one ProgressBar at the bottom of the screen for both initial load and pagination. I rejected this because the first time a user opens the app and sees an empty screen, a small spinner at the bottom looks broken. Instead, I implemented **two separate ProgressBars**: a centered one (`initialProgressBar`) for the first load, and a bottom one (`paginationProgressBar`) for subsequent page loads. This gives a much better first impression.

3. **`ViewModel` instead of `AndroidViewModel`** — AI initially suggested using a plain `ViewModel`, but I needed application-level `Context` to initialize `PreferenceManager` for the offline cache. Using `AndroidViewModel` gives access to the `Application` context without risking Activity context leaks — a deliberate choice I made over the AI's default suggestion.

4. **Client-side filtering for search** — AI suggested filtering the already-loaded articles locally in the adapter when the user types a search query. I rejected this because it limits results to only what's already been fetched. Instead, I implemented a proper API-driven search that sends the query directly to NewsAPI's `q` parameter, returning fresh, relevant results from the entire dataset — not just the articles already in memory.

---

## Example: Improving AI Output With My Own Judgment

**Scenario**: Search implementation approach.

AI suggested implementing search as a **local filter on the adapter** — essentially taking the list of articles already loaded and filtering them by title/description match on the client side. While this would be faster (no network call), I realized it had a major flaw: **the user would only be able to search within the articles already paginated into memory**, which could be as few as 20 items out of thousands available.

**My decision**: I replaced the local filter with a **full API-driven search**. When the user types a query, the app:
1. Cancels any pending search (debounce with 500ms delay)
2. Resets the pagination state (`currentPage = 1`, clears the article list)
3. Makes a fresh API call with the new query as the `q` parameter
4. Populates the list with genuinely relevant results from the server

This required more work — I had to wire up the `isInitialLoad` flag in the ViewModel, handle the clear button to reset back to the default query, and ensure smooth state transitions — but the result is a search feature that actually works as users expect.

---

## How AI Output Was Validated

- **Manual testing on emulator**: Ran the app on an Android emulator, tested pagination by scrolling through multiple pages, verified search with different keywords, and checked offline behavior by toggling airplane mode.
- **Code review**: Read through every AI-generated file line by line to ensure I understood the logic and could explain any part of it if asked. Refactored anything that felt unclear or overly complex.
- **Edge case testing**: Tested with empty search queries, very long search strings, rapid search input (to verify debounce), network disconnection mid-load, and API error responses to make sure all states (loading, error, empty) were handled correctly.
- **Logcat debugging**: Used OkHttp's logging interceptor and Android's Logcat to verify that API requests were being made with the correct parameters and pagination was incrementing properly.
