# Design Document - Hermes Travel App

## Sprint Schedule

| Sprint | Deliverable | Deadline | Weight |
|--------|-------------|----------|--------|
| **Sprint 1** | Splash Screen, Navigation, Screens, Scaffolding, Domain Model | 01/03/2026 | 0.8 pts |
| **Sprint 2** | Travel List (CRUD), Trip Itinerary (CRUD) | 15/03/2026 | 1.0 pts |
| **Sprint 3** | Data Persistence, Firebase Authentication, User Preferences | 12/04/2026 | 0.8 pts |
| **Sprint 4** | Login Screen, Authentication | 26/04/2026 | 1.0 pts |
| **Sprint 5** | Images, Documents, Hotel Reservations, Maps | 16/05/2026 | 1.4 pts |

---

## Architecture

### Pattern: MVVM (Model-View-ViewModel)

We chose MVVM because it's the recommended architecture for modern Android apps and works well with Jetpack Compose.

**Current Status (Sprint 03):**
- ✅ **View** — All screens implemented with Jetpack Compose
- ✅ **ViewModel** — All ViewModels implemented with Hilt injection
- ✅ **Repository** — Repository pattern with interface + implementation per domain
- ✅ **Room Database** — Full SQLite persistence via Room (replaces in-memory storage)
- ✅ **Firebase Auth** — Login, logout, register, password recovery
- ✅ **Navigation** — Two-level navigation (root + bottom tabs)
- ✅ **Domain Model** — Data classes fully defined

---

## Technology Stack

### Current (Sprint 03)
- **Kotlin** — Main programming language
- **Jetpack Compose** — Modern UI framework
- **Navigation Component** — Screen navigation
- **Material Design 3** — UI components and theming
- **Room** — SQLite persistence (DAO, Entities, TypeConverters)
- **Hilt** — Dependency injection
- **Firebase Auth** — Email/password authentication
- **Coroutines + Flow** — Async operations and reactive UI updates
- **SharedPreferences** — User preferences (language, dark mode)
- **Minimum SDK: API 26 (Android 8.0)** — Covers 94%+ of devices

### Future Sprints
- Image handling (Sprint 5)
- Interactive maps (Sprint 5)
- Hotel reservations (Sprint 5)

---

## Project Structure

```
hermes_travelapp/
├── ui/
│   ├── screens/          # All app screens (Compose)
│   ├── theme/            # Colors, typography, theme
│   └── viewmodels/       # ViewModels with Hilt
├── domain/
│   ├── model/            # Data classes (Trip, TripDay, ItineraryItem, User…)
│   ├── repository/       # Repository interfaces
│   └── ValidationUtils   # Date and field validation logic
└── data/
    ├── database/
    │   ├── dao/          # TripDao, TripDayDao, ItineraryItemDao, UserDao, AccessLogDao
    │   ├── entities/     # TripEntity, TripDayEntity, ItineraryItemEntity, UserEntity, AccessLogEntity
    │   ├── mapper/       # Extension functions toDomain() / toEntity()
    │   └── AppDatabase   # Room database class + TypeConverters
    ├── repository/       # Repository implementations (Room + Firebase)
    ├── fakeDB/           # Legacy in-memory sources (kept for reference)
    └── PreferencesManager # SharedPreferences wrapper
```

---

## Navigation

### Two-Level Navigation System

1. **Root Navigation** — Authentication and full-screen flows
   - Splash → Login → Register → Main
   - Main → Full-screen pages (TripOverview, DayItinerary, CreateTrip, Account, Preferences, About, Terms)

2. **Bottom Navigation** — Main app tabs (inside MainScreen)
   - Home, Explore, Trips, Favorites, Profile

```
Splash
  ├─► Login ──► ForgotPassword
  │     └─► Register
  └─► Main (bottom nav)
        ├─ Home
        ├─ Explore
        ├─ Trips ──► TripOverview ──► DayItinerary
        │              └─► CreateTrip
        ├─ Favorites
        └─ Profile ──► Account
                   ├─► Preferences
                   ├─► Terms
                   └─► About
```

---

## Implemented Screens

### Authentication Flow
- **SplashScreen** — App logo, loading bar, checks login state and routes accordingly
- **LoginScreen** — Email/password login via Firebase, error handling per error code
- **RegisterScreen** — Full registration form with field validation, sends email verification
- **ForgotPasswordScreen** — Sends password reset email via Firebase

### Main Tabs
- **HomeScreen** — Destination recommendations loaded from local JSON asset
- **ExploreScreen** — Search destinations by name, country, type or price
- **TripsScreen** — List of user trips with create/edit/delete actions
- **FavoritesScreen** — Saved recommendation items (in-memory per session)
- **ProfileScreen** — User info, navigation to account/preferences/about/terms, logout

### Trip Management
- **CreateTripScreen** — Create or edit a trip (dates via DatePicker, budget, description)
- **TripOverviewScreen** — Timeline view of trip days, budget overview, add/delete days
- **DayItineraryScreen** — Horizontal pager per day, activity list, add/edit/delete activities

### Profile & Settings
- **AccountScreen** — Edit username, birthdate (DatePicker), email, password
- **PreferencesScreen** — Language selector, dark mode toggle, currency, notifications
- **AboutScreen** — App version, tech specs, team members
- **TermsScreen** — Terms and conditions with accept/reject actions

---

## Database Schema (Sprint 03)

### Overview

The app uses **Room Database** (version 2) as the local persistence layer, replacing the in-memory storage from Sprint 02. Firebase Auth manages authentication exclusively; user profile data and trip data are stored locally in SQLite via Room.

**Database name:** `hermes_database`  
**Room version:** 2  
**Migration strategy:** `fallbackToDestructiveMigration` (development only — see Migration Strategy below)

---

### Entity Diagram

```
┌──────────────────────┐         ┌───────────────────┐         ┌───────────────────────┐
│        users         │ 1     * │      trips        │ 1     * │      trip_days        │
│──────────────────────│         │───────────────────│         │───────────────────────│
│ id (PK)              │────────►│ id (PK)           │────────►│ id (PK)               │
│ name                 │         │ title             │         │ trip_id (FK)          │
│ email                │         │ startDate         │         │ dayNumber  [INTEGER]  │
│ login                │         │ endDate           │         │ date       [LONG]     │
│ username             │         │ description       │         └───────────┬───────────┘
│ birthdate [LONG]     │         │ emoji             │                     │ 1
│ address              │         │ budget [INTEGER]  │                     │
│ country              │         │ spent  [INTEGER]  │                     │ *
│ phone                │         │ progress [REAL]   │         ┌───────────▼───────────┐
│ acceptEmails [INT]   │         │ daysRemaining     │         │    itinerary_items    │
│ profileInitials      │         │ user_id (FK)      │         │───────────────────────│
│ activeTripCount      │         └───────────────────┘         │ id (PK)               │
│ countriesVisited     │                                        │ trip_id               │
└──────────────────────┘                                        │ day_id (FK)           │
                                                                │ title      [TEXT]     │
┌──────────────────────┐                                        │ description [TEXT]    │
│      access_log      │                                        │ date       [LONG]     │
│──────────────────────│                                        │ time       [TEXT]     │
│ id (PK)              │                                        │ location   [TEXT]?    │
│ userId               │                                        │ cost       [REAL]?    │
│ datetime [LONG]      │                                        └───────────────────────┘
│ type [TEXT]          │
└──────────────────────┘
```

---

### Tables

#### `users`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | TEXT | PRIMARY KEY | Firebase UID or `"default_user"` during development |
| `name` | TEXT | NOT NULL | Full display name |
| `email` | TEXT | NOT NULL | Contact email address |
| `login` | TEXT | NOT NULL | Email used to log in (Firebase) |
| `username` | TEXT | NOT NULL | Unique username chosen by the user |
| `birthdate` | INTEGER | NOT NULL | Stored as epoch day (`LocalDate.toEpochDay()`) |
| `address` | TEXT | NOT NULL | Physical address |
| `country` | TEXT | NOT NULL | Country of residence |
| `phone` | TEXT | NOT NULL | Contact phone number |
| `acceptEmails` | INTEGER | NOT NULL | Whether the user accepts marketing emails (0/1) |
| `profileInitials` | TEXT | NOT NULL | Short initials, e.g. `"VS"` |
| `activeTripCount` | INTEGER | NOT NULL | Cached count of active trips |
| `countriesVisited` | INTEGER | NOT NULL | Cached count of visited countries |

#### `trips`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | TEXT | PRIMARY KEY | UUID generated client-side |
| `title` | TEXT | NOT NULL | Trip name |
| `startDate` | TEXT | NOT NULL | Format: `DD/MM/YYYY` |
| `endDate` | TEXT | NOT NULL | Format: `DD/MM/YYYY` |
| `description` | TEXT | NOT NULL | Free-text description |
| `emoji` | TEXT | NOT NULL | Single emoji, e.g. `"🏛️"` |
| `budget` | INTEGER | NOT NULL | Total budget in EUR |
| `spent` | INTEGER | NOT NULL | Amount spent so far in EUR |
| `progress` | REAL | NOT NULL | Completion ratio 0.0–1.0 |
| `daysRemaining` | INTEGER | NOT NULL | Days until trip start |
| `user_id` | TEXT | FK → users(id) ON DELETE CASCADE | Owner of this trip |

#### `trip_days`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | TEXT | PRIMARY KEY | UUID generated client-side |
| `trip_id` | TEXT | FK → trips(id) ON DELETE CASCADE | Parent trip |
| `dayNumber` | INTEGER | NOT NULL | Sequential day number (1, 2, 3…) |
| `date` | INTEGER | NOT NULL | Stored as epoch day (`LocalDate.toEpochDay()`) |

#### `itinerary_items`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | TEXT | PRIMARY KEY | UUID generated client-side |
| `trip_id` | TEXT | NOT NULL | Reference to parent trip |
| `day_id` | TEXT | FK → trip_days(id) ON DELETE CASCADE | Reference to specific day |
| `title` | TEXT | NOT NULL | Activity name |
| `description` | TEXT | NOT NULL | Activity details |
| `date` | INTEGER | NOT NULL | Stored as epoch day (`LocalDate.toEpochDay()`) |
| `time` | TEXT | NOT NULL | Stored as `"HH:mm"` string |
| `location` | TEXT | NULLABLE | Optional venue or address |
| `cost` | REAL | NULLABLE | Estimated cost in EUR |

#### `access_log`

| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | TEXT | PRIMARY KEY | UUID generated client-side |
| `userId` | TEXT | NOT NULL | ID of the user who performed the action |
| `datetime` | INTEGER | NOT NULL | Timestamp in epoch milliseconds |
| `type` | TEXT | NOT NULL | `"IN"` for login, `"OUT"` for logout |

---

### Relationships

| Relationship | Cardinality | Cascade |
|---|---|---|
| User → Trip | One-to-many | DELETE user → deletes all their trips |
| Trip → TripDay | One-to-many | DELETE trip → deletes all its days |
| TripDay → ItineraryItem | One-to-many | DELETE day → deletes all its activities |

Days are generated automatically from the trip's start/end dates via `generateDaysForTrip()` in `TripUtils.kt`. When dates change, days are regenerated after a confirmation dialog.

---

### Type Converters (`AppTypeConverters`)

Room cannot store `java.time` types natively. The following bidirectional converters are registered via `@ProvidedTypeConverter`:

| Kotlin Type | SQLite Column Type | To DB | From DB |
|---|---|---|---|
| `LocalDate` | `INTEGER` | `date.toEpochDay()` | `LocalDate.ofEpochDay(value)` |
| `LocalTime` | `TEXT` | `time.format("HH:mm")` | `LocalTime.parse(value, "HH:mm")` |

---

### Migration Strategy

During development, `fallbackToDestructiveMigration(false)` is used — on schema version mismatch the database is dropped and rebuilt. No data loss risk in development; the default user is re-seeded via `RoomDatabase.Callback.onCreate`.

For production (Sprint 04+), explicit migrations must be defined before incrementing the version number:

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // e.g. ALTER TABLE users ADD COLUMN phone TEXT
    }
}

Room.databaseBuilder(context, AppDatabase::class.java, "hermes_database")
    .addMigrations(MIGRATION_1_2)
    .build()
```

---

### Data Access Objects (DAOs)

#### `TripDao`

| Method | Return | Description |
|---|---|---|
| `getTripsByUser(userId)` | `Flow<List<TripEntity>>` | All trips for one user, reactive |
| `getTripById(tripId)` | `TripEntity?` | Single trip lookup |
| `existsByTitle(userId, title)` | `Boolean` | Checks for duplicate trip name per user |
| `insertTrip(trip)` | `suspend` | Insert with `REPLACE` on conflict |
| `updateTrip(trip)` | `suspend` | Full entity update |
| `deleteTrip(trip)` | `suspend` | Delete by entity object |
| `deleteTripById(tripId)` | `suspend` | Delete by ID |
| `getTripsWithDays(userId)` | `Flow<List<TripWithDays>>` | @Transaction join query |

#### `TripDayDao`

| Method | Return | Description |
|---|---|---|
| `getDaysForTrip(tripId)` | `Flow<List<TripDayEntity>>` | All days ordered by dayNumber, reactive |
| `insertTripDay(day)` | `suspend` | Insert with `REPLACE` on conflict |
| `deleteDayById(dayId)` | `suspend` | Delete single day |
| `deleteDaysByTripId(tripId)` | `suspend` | Clear all days for a trip |
| `getLastDayForTrip(tripId)` | `TripDayEntity?` | Last day by dayNumber DESC |

#### `ItineraryItemDao`

| Method | Return | Description |
|---|---|---|
| `getActivitiesForDay(dayId)` | `Flow<List<ItineraryItemEntity>>` | Activities ordered by time ASC, reactive |
| `insertActivity(activity)` | `suspend` | Insert with `REPLACE` on conflict |
| `updateActivity(activity)` | `suspend` | Full entity update |
| `deleteActivity(activity)` | `suspend` | Delete by entity object |
| `deleteActivityById(activityId)` | `suspend` | Delete by ID |

#### `UserDao`

| Method | Return | Description |
|---|---|---|
| `insertUser(user)` | `suspend` | Insert with `REPLACE` on conflict |
| `getUserById(id)` | `UserEntity?` | Single user lookup by ID |
| `getUserByEmail(email)` | `UserEntity?` | Single user lookup by email |
| `updateUser(user)` | `suspend` | Full entity update |
| `deleteUser(id)` | `suspend` | Delete user by ID |
| `isUsernameTaken(username)` | `Boolean` | Checks if username already exists |
| `getAllUsers()` | `Flow<List<UserEntity>>` | All users, reactive |

#### `AccessLogDao`

| Method | Return | Description |
|---|---|---|
| `insertLog(log)` | `suspend` | Insert a new access log entry |
| `getLogsByUser(userId)` | `Flow<List<AccessLogEntity>>` | All logs for a user ordered by datetime DESC |

---

### Multi-user Support

Trips are scoped to the authenticated user via the `user_id` column in the `trips` table. The DAO filters by owner:

```sql
SELECT * FROM trips WHERE user_id = :userId
```

The real Firebase UID is passed via `authRepository.getCurrentUserId()` in `TripRepositoryImpl`. A `"default_user"` is pre-seeded on database creation as a fallback for unauthenticated states during development.

---

### Dependency Injection (Hilt)

Room components are provided as singletons via `DatabaseModule`:

| Provider | Scope | Returns |
|---|---|---|
| `provideDatabase(context, typeConverters)` | `@Singleton` | `AppDatabase` |
| `provideTripDao(database)` | `@Singleton` | `TripDao` |
| `provideTripDayDao(database)` | `@Singleton` | `TripDayDao` |
| `provideItineraryItemDao(database)` | `@Singleton` | `ItineraryItemDao` |
| `provideUserDao(database)` | `@Singleton` | `UserDao` |
| `provideAccessLogDao(database)` | `@Singleton` | `AccessLogDao` |

Firebase components are provided via `FirebaseModule`:

| Provider | Scope | Returns |
|---|---|---|
| `provideFirebaseAuth()` | `@Singleton` | `FirebaseAuth` |

Repository bindings are declared in `RepositoryModule` using `@Binds`, mapping each interface to its implementation.

---

## Firebase Authentication (Sprint 03)

### Implemented Operations

| Operation | Method | Screen |
|---|---|---|
| Sign in | `signInWithEmailAndPassword` | LoginScreen |
| Register | `createUserWithEmailAndPassword` | RegisterScreen |
| Email verification | `sendEmailVerification` | Post-register |
| Password reset | `sendPasswordResetEmail` | ForgotPasswordScreen |
| Sign out | `firebaseAuth.signOut()` | ProfileScreen |
| Session check | `firebaseAuth.currentUser != null` | SplashScreen |

### Error Handling

Firebase errors are mapped from `FirebaseAuthException.errorCode` to localized string resources in both `AuthViewModel` and the UI screens. All operations are logged via Logcat (`Log.d/i/e`) in `AuthRepositoryImpl`.

---

## Data Validation (Sprint 03)

### Implemented Validations

| Validation | Where | Description |
|---|---|---|
| Duplicate trip name | `TripRepositoryImpl.addTrip()` | Calls `existsByTitle()` before insert |
| Start date before end date | `TripViewModel.validateTrip()` | Parsed with `DateTimeFormatter` |
| Date format | `TripViewModel.validateTrip()` | Catches `DateTimeParseException` |
| Activity date within trip range | `ValidationUtils.validateActivityDate()` | Compares with trip start/end |
| Email format | `ValidationUtils.isValidEmail()` | Uses `Patterns.EMAIL_ADDRESS` |
| Username uniqueness | `UserRepositoryImpl.createUser()` | Calls `isUsernameTaken()` before insert |
| Password length | `AuthViewModel.registerWithFirebase()` | Minimum 6 characters |
| Password confirmation | `AuthViewModel.registerWithFirebase()` | Checks password == confirmPassword |

---

## Design Decisions

### Why Room over raw SQLite?
Room provides compile-time query verification, type-safe DAO interfaces, and native `Flow` support for reactive UI — avoiding manual cursor management.

### Why `Flow` in DAOs?
`Flow<List<T>>` lets the UI automatically recompose when the database changes, without manual refresh calls. ViewModels collect these flows into `StateFlow` exposed to Composables.

### Why `fallbackToDestructiveMigration` in development?
Schema changes during active development are frequent. Destructive migration avoids writing migration scripts for every intermediate version. This will be replaced with explicit migrations before any production release.

### Why `UUID.randomUUID()` for entity IDs?
Client-side UUID generation avoids round-trips to the server for ID assignment and supports offline-first operation. There is no risk of collision in single-user local storage.

### Why SharedPreferences for user preferences?
Preferences such as language code and dark mode are small, scalar values that do not need relational queries or reactive flows. SharedPreferences is the simplest and most appropriate tool for this use case.

### Why a separate `access_log` table?
Persisting every login and logout event with userId and timestamp allows auditing of user sessions without polluting the `users` table with session-specific data.

---

## Testing (Sprint 03)

### Instrumented Tests (`androidTest`)

All DAO tests run against a Room in-memory database (`Room.inMemoryDatabaseBuilder`) to avoid side effects on the real database.

| Test Class | DAOs Covered |
|---|---|
| `DatabaseTests` | `TripDao`, `UserDao`, `AccessLogDao`, `ItineraryItemDao` |

### Test Coverage

| Test | What it validates |
|---|---|
| `testUserPersistence` | Insert and query user |
| `testTripConstraintAndFiltering` | Insert trip and filter by userId |
| `testAccessLogPersistence` | Insert and query access log |
| `testUsernameUniqueness` | `isUsernameTaken()` returns true after insert |
| `testTripUpdate` | Trip title and budget update persists |
| `testTripDelete` | Trip deleted by entity object |
| `testTripDeleteById` | Trip deleted by ID string |
| `testTripInvalidDates` | Room does not enforce date order (validated at ViewModel layer) |
| `testDuplicateTripNamePrevention` | `existsByTitle()` returns true for existing name |
| `testValidationUtils` | `validateTripDates()` returns error for invalid range, null for valid |
| `testInsertAndQueryActivity` | Insert activity and query by dayId |
| `testUpdateActivity` | Activity title and time update persists |
| `testDeleteActivity` | Activity deleted by entity object |
| `testDeleteActivityById` | Activity deleted by ID string |
| `testActivitiesOrderedByTime` | Activities returned in ascending time order |
| `testActivitiesFilteredByDayId` | Activities correctly isolated by dayId |

---

## Next Steps

### Sprint 04 (26/04/2026)
- Persist full user profile edits from `AccountScreen` to the `users` Room table
- Implement profile photo upload and storage

### Sprint 05 (16/05/2026)
- Camera integration for trip photos
- PDF document upload and storage
- Hotel reservation features
- Interactive maps with destination coordinates

---

**Version:** 3.0  
**Authors:** Ivan Gil Cañizares, Marco Beruet Morelli  
**Course:** Aplicaciones para Dispositivos Móviles (105025-2526)  
**Institution:** Universitat de Lleida — Campus Igualada
