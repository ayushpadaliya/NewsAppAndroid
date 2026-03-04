# 📰 NewsApp – Android News Reader

A simple, production-ready Android news application that fetches and displays live news articles from [NewsAPI.org](https://newsapi.org/). Built with **Clean Architecture**, it features paginated article browsing, real-time search with debounce, offline caching, and a polished detail view — all while handling loading, error, and empty states gracefully.

---

## 🚀 Setup Instructions

### Prerequisites
- **Android Studio** Meerkat (2025.1+) or later
- **JDK 11** or higher
- **Android SDK 36** (compileSdk)
- A free API key from [NewsAPI.org](https://newsapi.org/register)

### Steps to Run
1. **Clone the repository**
   ```bash
   git clone https://github.com/ayushpadaliya/NewsAppAndroid.git
   cd NewsAppAndroid
   ```
2. **Add your API key**  
   Open `app/src/main/java/com/example/newsapp/utils/VariableBag.kt` and replace the API key value:
   ```kotlin
   const val API_KEY = "YOUR_API_KEY_HERE"
   ```
3. **Open in Android Studio** and let Gradle sync.
4. **Run** on an emulator or a physical device (minSdk 24 / Android 7.0+).

---

## 🏗️ Architecture Overview

The project follows **Clean Architecture** principles with clear separation across three layers:

```
com.example.newsapp
├── dashboard
│   ├── data                    # Data Layer
│   │   ├── model/              # API response models & request params
│   │   └── repository/         # Repository – fetches from API, falls back to cache
│   ├── domain                  # Domain Layer
│   │   ├── mapper/             # Maps API response → domain models
│   │   ├── model/              # Domain models (UI-facing)
│   │   └── usecase/            # Business logic (single entry point for ViewModel)
│   └── presentation            # Presentation Layer
│       ├── adapter/            # RecyclerView adapter with loading footer
│       ├── view/               # Activities (NewsFeedActivity, ViewNewsDetailsActivity)
│       └── viewmodels/         # ViewModel with pagination & state management
├── network                     # Networking
│   ├── ApiServices.kt          # Retrofit API interface
│   └── RetrofitClient.kt       # Singleton Retrofit instance with OkHttp logging
└── utils                       # Utilities
    ├── PreferenceManager.kt    # SharedPreferences wrapper for offline cache
    ├── Resource.kt             # Sealed class for Loading / Success / Error states
    └── VariableBag.kt          # App-wide constants (base URL, API key, pref keys)
```

### Data Flow
```
API (NewsAPI.org) → Repository → UseCase → ViewModel → Activity/Adapter
                         ↕
                 SharedPreferences
                   (Offline Cache)
```

1. **ViewModel** calls the **UseCase** with search params and page number.
2. **UseCase** delegates to the **Repository**.
3. **Repository** makes the API call via **Retrofit**. On success, it caches the response in **SharedPreferences** and maps it to domain models using the **Mapper**. On failure (network error / API error), it falls back to the cached data.
4. **ViewModel** emits the result wrapped in the **`Resource`** sealed class (Loading → Success / Error).
5. **Activity** observes LiveData and updates the UI accordingly — showing loading spinners, the article list, or an empty/error state with a Lottie animation.

---

## 🔑 Key Decisions & Trade-offs

| Decision | Rationale |
|---|---|
| **Clean Architecture (Data → Domain → Presentation)** | Ensures separation of concerns and makes the codebase scalable. The domain layer stays independent of frameworks, making business logic easily testable. |
| **SharedPreferences for offline cache** | Lightweight and sufficient for caching a single API response. Avoids the complexity of Room for this scope, while still providing a usable offline experience. |
| **Manual DI instead of Hilt/Dagger** | Keeps the project simple and dependency-light for a small codebase. Dependencies are created in the ViewModel. In a larger app, I would adopt Hilt for lifecycle-aware DI. |
| **`AndroidViewModel` over `ViewModel`** | Needed application context to initialize `PreferenceManager` for offline caching without leaking Activity context. |
| **ViewBinding + DataBinding** | Type-safe view access that eliminates `findViewById` boilerplate. DataBinding is used in layout XML wrappers. |
| **Mapper pattern (API → Domain models)** | Decouples the UI from the raw API response. If the API schema changes, only the mapper needs updating — the rest of the app remains untouched. |
| **Sealed `Resource<T>` class** | Provides a type-safe, exhaustive way to represent Loading, Success, and Error states in the ViewModel → UI contract. |
| **RecyclerView scroll-based pagination** | Provides a seamless infinite scroll experience without needing another library. The ViewModel tracks `currentPage` and `isLastPage` to prevent redundant requests. |
| **Search debounce with coroutines** | Waits 500ms after the user stops typing before firing a search request, reducing unnecessary API calls and improving UX. |
| **SDP/SSP for dimensions** | Ensures UI elements scale properly across different screen sizes and densities. |

---

## ⚙️ Tech Stack

| Category | Library / Tool |
|---|---|
| Language | Kotlin |
| Architecture | Clean Architecture (MVVM) |
| Networking | Retrofit 3.0 + OkHttp Logging Interceptor |
| JSON Parsing | Gson Converter |
| Image Loading | Glide 4.16 |
| Animations | Lottie 6.6.2 (empty state) |
| Responsive Sizing | SDP & SSP (Intuit) |
| State Management | LiveData + sealed `Resource` class |
| Offline Storage | SharedPreferences |
| UI Refresh | SwipeRefreshLayout |
| View Access | ViewBinding + DataBinding |
| Min SDK | 24 (Android 7.0) |

---

## ⚠️ Known Limitations

- **Offline cache is shallow**: Only the last successful API response is cached. Paginated or search-specific results are not individually cached.
- **No dependency injection framework**: Dependencies are manually wired in the ViewModel. This works for the current scope but would not scale well for a larger project.
- **API key is hardcoded in source**: Ideally, this should be stored in `local.properties` or retrieved from a secure backend. For this assignment, it is kept in `VariableBag.kt` for simplicity.
- **No unit/instrumentation tests**: Given the time constraint, tests were not included. The architecture is designed to be test-friendly — the UseCase and Repository can be easily unit-tested with mock dependencies.
- **No Room database**: SharedPreferences is used as a quick caching mechanism. For richer offline support (e.g., caching per-query results, full article content), Room would be more appropriate.

---

## 🗂️ API Reference

**Endpoint used**: `GET https://newsapi.org/v2/everything`

| Parameter | Value | Description |
|---|---|---|
| `q` | User's search query (default: `tesla`) | Keyword to search for |
| `sortBy` | `publishedAt` | Sort results by publication date |
| `apiKey` | Your API key | Authentication |
| `page` | 1, 2, 3, ... | Pagination support |

Full documentation: [https://newsapi.org/docs](https://newsapi.org/docs)

---

## 📄 License

This project was built as a take-home assignment and is not intended for commercial use.