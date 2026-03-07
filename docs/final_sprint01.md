# Sprint 1 Retrospective - Hermes Travel App

**Project:** Hermes Travel App**Team:** Ivan Gil Cañizares, Marco Beruet Morelli

## 1. Accomplishments (Reviewing the Backlog)

Following the **plan_sprint01.md**, we have successfully completed the foundational phase:

*   **Project Setup & Version Control (T1 & T2):** 
    *   Repository initialized on GitHub with Apache 2.0 License, CONTRIBUTING.md, and a structured `/docs` folder.
    *   Logo generated and integrated into the project resources.
*   **Core Navigation & Structure (T3 - Marco):**
    *   Implemented `NavGraph.kt` with a two-level navigation system (Root + Bottom Navigation).
    *   Configured the 5-tab `NavigationBar` for seamless screen switching.
*   **UI Screens & Layouts (T3 & T4 - Ivan & Marco):**
    *   Completed the `SplashScreen` with the official logo (Ivan).
    *   Developed the `HomeScreen` dashboard and `ExploreScreen` (Ivan).
    *   Implemented `TripsScreen`, `FavoritesScreen`, and the `PreferencesScreen` UI (Marco).
    *   All layouts follow **Material Design 3** guidelines with the custom "Hermes" palette.
*   **Domain Model Definition (T3):**
    *   Created all data classes: `User`, `Trip`, `ItineraryItem`, `FavoritePlace`, and `Preferences`.
    *   Applied the agreed-upon documentation style: KDoc headers and `// @TODO` placeholders for future logic.

## 2. Technical Review
*   **Architecture:** Confirmed use of **MVVM**. The domain layer is strictly separated from the UI.
*   **Theming:** Successfully documented the color palette in `color-palette.md`, utilizing *DoradoAtenea* and *AzulOscuro* as primary brand identifiers.
*   **Dependencies:** Project is running on Kotlin 2.2.10 and Compose BOM 2024.09.00.

## 3. Retrospective Analysis

### What went well?
*   **Task Distribution:** The 50/50 workload split defined in the planning phase was maintained, allowing both members to contribute equally to the UI and the logic.
*   **Design Consistency:** The "Greek-modern" aesthetic is consistent across all 13 planned screens.
*   **Navigation Flow:** The transition from Splash -> Home and between tabs is smooth and bug-free.

### What can be improved?
*   **Hardcoded Data:** As planned for Sprint 1, data is still hardcoded. In Sprint 2, we need to transition to dynamic data handling.
*   **String Externalization:** Some UI labels are not yet in `strings.xml`. This is a priority for the next sprint to prepare for internationalization.

## 4. Final Thoughts
The goals set in `plan_sprint01.md` have been met. The app has a professional "skeleton" and a clear visual identity. We are ready to begin Sprint 2: Travel List & Itinerary CRUD.

---
**Prepared by:** Ivan Gil Cañizares & Marco Beruet Morelli
