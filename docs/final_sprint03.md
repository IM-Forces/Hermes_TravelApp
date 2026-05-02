# Sprint 3 Retrospective - Hermes Travel App

**Project:** Hermes Travel App  
**Team:** Ivan Gil Cañizares, Marco Beruet Morelli

## 1. Accomplishments (Reviewing the Backlog)

Following the **plan_sprint03.md**, we have successfully completed all tasks of the persistence and authentication phase:

- **SQLite Persistence with Room (T1 - Ivan & Marco):**
  - Replaced all in-memory storage (`FakeTripDataSource`, `FakeActivityDataSource`) with a full Room Database implementation.
  - Defined all required entities: `TripEntity`, `TripDayEntity`, `ItineraryItemEntity`, `UserEntity`, and `AccessLogEntity`. Each entity contains at least one datetime field, one text field, and one integer field as required.
  - Implemented `AppTypeConverters` using `@ProvidedTypeConverter` to handle `LocalDate` (stored as epoch day) and `LocalTime` (stored as `"HH:mm"` string).
  - Created DAOs for all entities: `TripDao`, `TripDayDao`, `ItineraryItemDao`, `UserDao`, and `AccessLogDao`, each with full CRUD operations using `@Insert`, `@Update`, `@Delete`, and `@Query` annotations.
  - All read operations return `Flow<List<T>>` for reactive UI updates via `collectAsState()`.
  - Updated `TripViewModel` and `ActivityViewModel` to use Room DAOs via the Repository pattern.
  - The architecture flow `UI → ViewModel → Repository → DAO → Room Entity` is fully respected.

- **Login and Logout with Firebase (T2 - Ivan & Marco):**
  - Connected the app to Firebase and integrated `google-services.json`.
  - Implemented `LoginScreen` with email/password authentication via `signInWithEmailAndPassword()`.
  - On app start, `SplashScreen` checks `firebaseAuth.currentUser != null` — authenticated users are redirected directly to the main screen, unauthenticated users to the login screen.
  - Implemented logout via `firebaseAuth.signOut()` accessible from `ProfileScreen`, redirecting to `LoginScreen`.
  - All authentication operations are logged in Logcat at the appropriate levels (`Log.d`, `Log.i`, `Log.e`) in `AuthRepositoryImpl`.

- **Register and Password Recovery (T3 - Ivan & Marco):**
  - Implemented `RegisterScreen` with full field validation: required fields, email format, password minimum length (6 characters), and password confirmation match.
  - Registration uses `createUserWithEmailAndPassword()` via Firebase Auth, followed by an automatic `sendEmailVerification()` call.
  - All authentication logic is encapsulated in `AuthRepository` and `AuthRepositoryImpl`, following the Repository design pattern.
  - Implemented `ForgotPasswordScreen` with `sendPasswordResetEmail()` via Firebase.
  - Error codes from `FirebaseAuthException` are mapped to localized string resources across all three languages (English, Spanish, Catalan).

- **User Persistence and Multi-user Support (T4 - Ivan & Marco):**
  - Created `UserEntity` with all required fields: `login`, `username`, `birthdate` (date field stored as epoch day), `address`, `country`, `phone`, and `acceptEmails`.
  - Implemented `UserDao` with username uniqueness check via `isUsernameTaken()` — validated before every insert in `UserRepositoryImpl`.
  - Added `user_id` foreign key to `TripEntity`, linking each trip to its owner. `TripDao` filters trips by `userId` so each user only sees their own trips.
  - Created `AccessLogEntity` and `AccessLogDao` to persist every login and logout event with `userId`, `datetime` (epoch milliseconds), and `type` (`"IN"` / `"OUT"`). Logs are inserted automatically in `AuthRepositoryImpl` on every `signIn()`, `register()`, and `signOut()` call.
  - Updated `design.md` with the full database schema, entity relationships, DAO method reference, and migration strategy.

- **Testing and Debugging (T5 - Marco):**
  - Written and passed **16 instrumented tests** in `DatabaseTests` using Room in-memory database (`Room.inMemoryDatabaseBuilder`) with `AppTypeConverters` injected.
  - Tests cover full CRUD for `TripDao`, `UserDao`, `AccessLogDao`, and `ItineraryItemDao`.
  - A shared `setupFullHierarchy()` helper correctly seeds the User → Trip → TripDay chain required by foreign key constraints before each activity test.
  - Data validation tests confirm that `existsByTitle()` correctly prevents duplicate trip names and that `ValidationUtils.validateTripDates()` rejects invalid date ranges.
  - All CRUD operations in `TripRepositoryImpl`, `ActivityRepositoryImpl`, `TripDayRepositoryImpl`, and `AuthRepositoryImpl` include `Log.d` (operation start), `Log.i` (success), and `Log.e` (error with exception) statements, verified via Logcat.

## 2. Technical Review

- **Architecture:** The MVVM pattern with Repository layer is fully maintained. No business logic is placed in the UI. Room DAOs are injected via Hilt (`@HiltViewModel`, `@Inject`, `@Singleton`).
- **Room Database:** Version 2, `fallbackToDestructiveMigration` used during development. A `"default_user"` is pre-seeded via `RoomDatabase.Callback.onCreate` as a development fallback.
- **Firebase Auth:** Exclusively manages authentication. User profile data is persisted separately in the local Room `users` table on registration.
- **Dependency Injection:** Hilt is used throughout — `DatabaseModule` provides all DAOs and the `AppDatabase` as singletons; `FirebaseModule` provides `FirebaseAuth`; `RepositoryModule` binds all interfaces to their implementations via `@Binds`.
- **Testing Stack:** Room in-memory database, `AndroidJUnit4`, `runBlocking`, `kotlinx.coroutines.flow.first()`.
- **Test Results:** 16 instrumented tests — 0 failures, 0 errors.

| Test | Passed | Total |
|---|---|---|
| `testUserPersistence` | ✅ | 1 |
| `testTripConstraintAndFiltering` | ✅ | 1 |
| `testAccessLogPersistence` | ✅ | 1 |
| `testUsernameUniqueness` | ✅ | 1 |
| `testTripUpdate` | ✅ | 1 |
| `testTripDelete` | ✅ | 1 |
| `testTripDeleteById` | ✅ | 1 |
| `testTripInvalidDates` | ✅ | 1 |
| `testDuplicateTripNamePrevention` | ✅ | 1 |
| `testValidationUtils` | ✅ | 1 |
| `testInsertAndQueryActivity` | ✅ | 1 |
| `testUpdateActivity` | ✅ | 1 |
| `testDeleteActivity` | ✅ | 1 |
| `testDeleteActivityById` | ✅ | 1 |
| `testActivitiesOrderedByTime` | ✅ | 1 |
| `testActivitiesFilteredByDayId` | ✅ | 1 |
| **TOTAL** | **16** | **16** |

## 3. Retrospective Analysis

### What went well?

- **Task Distribution:** The 50/50 workload split was maintained. Ivan led Firebase integration and ViewModel updates; Marco led the Room infrastructure, DAOs, and testing.
- **Architecture Compliance:** The `UI → ViewModel → Repository → DAO` flow was consistently respected throughout the sprint, making the codebase modular and easy to test.
- **Foreign Key Design:** The cascading delete chain (`User → Trip → TripDay → ItineraryItem`) simplifies data cleanup and was implemented correctly using `@ForeignKey(onDelete = CASCADE)`.
- **Reactive UI:** Using `Flow` in all DAOs combined with `collectAsState()` in Composables ensures the UI reflects database changes automatically without manual refresh calls.
- **Test Infrastructure:** The `setupFullHierarchy()` helper avoided repetition across activity tests and correctly handled the foreign key insertion order.
- **Error Handling:** Firebase error codes are fully mapped to user-facing messages in all three supported languages, providing a polished authentication experience.

### What can be improved?

- **Favorites Persistence:** The favorites feature is still stored in memory per session. It should be migrated to a Room table in a future sprint.
- **AccountScreen → Room:** Edits made in `AccountScreen` (username, birthdate, email) are currently saved only to `SharedPreferences`. In Sprint 04, these should also update the `users` Room table.
- **Instrumented vs Unit Tests:** All current tests are instrumented (require a device or emulator). Future sprints should add pure JVM unit tests for ViewModel and Repository logic to speed up the test feedback loop.

## 4. Final Thoughts

All goals set in `plan_sprint03.md` have been met. The application now has full SQLite persistence via Room, Firebase email/password authentication with registration and password recovery, multi-user trip isolation, access log persistence, and a complete suite of 16 passing instrumented DAO tests. The codebase is clean, modular, and ready for Sprint 04.

---
**Prepared by:** Ivan Gil Cañizares & Marco Beruet Morelli
