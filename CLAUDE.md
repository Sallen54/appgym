# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

AppGym: a personal Android gym tracker for one user (Alejandro, 181 cm, ~75 kg, returning to training after years of volleyball). Kotlin + Jetpack Compose + Room. No backend, no login, no network at runtime — everything lives on the phone.

Because it has exactly one user, there is no need for onboarding flows, empty-account states, or multi-user abstractions. Optimise for **someone using this one-handed, mid-set, in a noisy gym**.

The UI language is **Spanish**. All user-facing strings, exercise names, and code comments are in Spanish. Identifiers are English except in the newer screens, where Spanish names crept in (`PlanCard`, `DiaFila`, `HoraChip`) — match the surrounding file rather than renaming.

## Commands

```bash
./gradlew assembleDebug            # build
./gradlew installDebug             # build + install on connected device
./gradlew test                     # JVM unit tests, no device needed (~seconds)
./gradlew connectedAndroidTest     # Room/repository tests, needs device or emulator
./gradlew test --tests "com.alejandro.appgym.FormatTest"                       # one class
./gradlew test --tests "com.alejandro.appgym.FormatTest.*volumen*"             # one test
```

There is no linter or formatter configured. Android Studio's default Kotlin style applies.

## Architecture

Single-module, single-activity, unidirectional data flow:

```
Room (AppDatabase, DAOs)
  └── GymRepository        all suspend functions and Flows; no Android imports beyond Context
        └── GymViewModel   one shared AndroidViewModel for the whole app; exposes StateFlow
              └── AppRoot  NavHost + bottom bar, passes `vm` down to every screen
                    └── screens/
```

The single ViewModel is deliberate. Every screen receives the same `vm` instance from `AppRoot`, so state stays consistent across tabs without a shared-state layer. Don't introduce per-screen ViewModels.

`Fields.kt`, and the `PrimaryAction` / `SecondaryAction` / `GhostAction` / `IconAction` composables inside `TodayScreen.kt`, are shared across screens despite living in screen files. Import them rather than writing new button variants.

### Data model

Four tables. `WorkoutSession` (one per calendar day — enforced by a **unique index on `date`**), `SetEntry` (one row per set: reps/weight for strength, or `minutes` plus `distanceKm`/`calories`/`avgHeartRate` for cardio, copied off the machine's end screen), `Routine` + `RoutineItem` (templates), `BodyWeight` (unique index on `date`, so re-logging the same day overwrites).

Profile (name, height, age) lives in `SharedPreferences`, not Room. Age drives the heart-rate zones in `Profile.zoneOf()`, which use the Tanaka formula (`208 − 0.7 × age`) rather than the cruder `220 − age`.

A session with `restDay = true` is a deliberately marked rest day, which is different from a day with no session at all. `ReportBuilder` excludes them from the training-frequency figures.

Dates are stored as `LocalDate.toEpochDay()` (Long), times as minutes since midnight (Int, `-1` = unset). `WorkoutSession.durationMinutes` already handles sessions that cross midnight.

### Two things that will bite you

**Room migrations.** The DB is at version 7 with real user data on a physical device. Every schema change needs a `Migration` added to `addMigrations(...)` in `AppDatabase.kt`. `fallbackToDestructiveMigration()` is set as a last resort — if you rely on it, Alejandro loses his training history. Test the migration against a populated database before shipping.

**Seeded plans.** `SeedData.kt` holds the plan the app installs on first run (currently one: "Grasa y músculo"; an adaptation plan was retired once Alejandro finished it). Changing them requires bumping `SeedData.VERSION`; `GymRepository.reseedPlan()` then swaps them in. That function deliberately preserves (a) routines the user created by hand (`planKey` is blank) and (b) old seeded routines already referenced by a logged session. Keep both guarantees. Retired routine names must be added to `SeedData.obsoleteNames` or they linger forever, and `DEFAULT_KEY` must always point at a plan that still exists — `GymViewModel` falls back to it if the stored `activePlan` no longer resolves.

### Exercise catalog

`ExerciseCatalog.kt` is a hand-written static list of 92 exercises. Each `id` must have a matching `app/src/main/assets/exercises/<id>.webp`. Images load through `AssetImage`, which keeps its own `LruCache` — there is no Coil/Glide dependency and adding one is not necessary.

`alternativesFor(id)` suggests substitutes when a machine is taken or a movement hurts. It scores candidates by shared muscles rather than by similar names, but the catalog's muscle data is coarse (every deltoid exercise is just "Hombros"), so two hand-written tables override it: `preferidas` for cases the scoring gets wrong, and `opuestos` to stop antagonist pairs being offered as substitutes for each other — abductors were being suggested to replace adductors. Add to those tables rather than tuning the weights.

## Design system

Defined in `ui/theme/`. Material 3, dark only — there is no light theme and no dynamic color. The values come from a design handoff (`design_handoff_rediseno_amigable/`, which contains the spec, an HTML reference and 2× screenshots of the five redesigned screens). Treat that folder as the source of truth for visual questions.

**Colour — warm dark.** Background `Ink #171511`, card `Surface1 #211E18`, raised card / in-card input `Surface2 #2A2620`, control surface `Control #23211A`. Hierarchy comes from these four steps: **there are no shadows anywhere**, and borders appear only where genuinely needed (`Outline #322D25`, or `VoltBorder` on an active card).

One accent, `Volt #CBF24C`, on `OnVolt #191D07`. It is rationed on purpose: the only lime-filled controls are the "Marcar serie" button, the FAB, "Registrar", and the "Empezar" of the day that is due today. Everything else is `Control`.

Nine muscle-group colours (`groupColor()`) that are *identification, not decoration*: they show up as one line of text under an exercise name and as the record weight, never as fills or bars over images. `WeekRamp` is the six-step green-to-lime ramp for the weekly chart.

**Type — Outfit**, bundled in `res/font/` (SIL OFL, licence in `assets/OFL-Outfit.txt`). Instanced as four static weights from the variable font, so no downloadable-fonts dependency. The Material scale is remapped by role, not by name: `headlineLarge` = screen title 28sp, `headlineSmall` = featured card 20sp, `titleLarge` = section heading 17sp, `titleMedium` = row title 15.5sp, `displayMedium` = big figure 32sp. Only tokens that exist in `Type.kt` should be used — anything else silently falls back to the system font.

**Uppercase is banned except in `Badge`.** The old all-caps overlines are gone; section headings are now normal sentence-case text. `labelSmall` exists only to serve `Badge` (PLAN ACTIVO, FASE ACTUAL) and the "SERIE 3" label.

**Shape.** `AppShapes`: cards `large` 24 dp, inner rows `medium` 18 dp, inputs/thumbnails `small` 14 dp, steppers `extraSmall` 11 dp. Buttons, chips, badges and FABs use `Pill` (99 dp), not a rounded rectangle.

**Spacing.** Screen gutter is `Gutter` (22 dp), applied per-item inside `LazyColumn` (not on the list) so backgrounds can still bleed edge to edge. 10–12 dp between cards, 16–18 dp of card padding; `SectionTitle` already carries 26 dp above and 12 dp below.

### Design constraints that came from the user, not from taste

These are the standing rules. Breaking them is a regression:

- **No emoji anywhere.** He asked for this explicitly and called out "the typical emojis that AI gives you". Use Material rounded icons.
- **Nothing that looks generic or AI-generated.** Charts are hand-drawn on `Canvas` (`Charts.kt`) rather than pulled from a library, and the type scale is customised rather than default. Keep that bar.
- **Copy is plain Spanish, not gym jargon or app-speak.** "Peso movido", not "Volumen". "Añadir ejercicio suelto", not "Añadir".
- **Match the conventions of real gym apps** (Strong, Hevy) over inventing new ones. Weight moved is displayed in **kg with a thousands separator** (`12.450 kg`) — an earlier version showed tonnes and was rejected as unintelligible.
- **Destructive actions need a confirmation dialog** ("Vaciar el día", "Quitar ejercicio") — *except* deleting one set, which deletes immediately and offers Undo in a snackbar. One tap to undo beats a dialog on every set.
- **The app is used mid-set, one-handed.** Prefer steppers over the keyboard, big tap targets, and fewer things on screen at once.

### How logging a set works

`TodayScreen` is an accordion. Only one exercise is expanded — by default the first with sets outstanding, derived from the data rather than stored, so it survives recomposition. Inside it:

- Done sets collapse to a one-line row (`SerieHecha`): tapping the check undoes it, tapping the × deletes with Undo.
- The first set not done is the **active** set: a bordered block with ±2.5 kg and ±1 rep steppers, and a lime "Marcar serie" button. Tapping the number opens the numeric keyboard.
- Marking the last set collapses the whole card to "Completado".

`vm.deleteSetWithUndo` stashes the entry in `ultimaBorrada`; `AppRoot` observes it and shows the snackbar. Deleting and restoring keeps the original `setNumber` and `position`.

### Known UX weak points worth improving

- `TodayScreen.kt` is the largest file and holds the header, week strip, exercise accordion, set rows, both dialogs and the buttons. It is the one most in need of splitting.
- `Historial`, `ExerciseDetail`, `RoutineDetail` and `Picker` were not part of the redesign. They now use the new tokens but their layout was not reworked — the handoff explicitly leaves them pending.
- The week strip shows a fixed window around the selected date; the calendar button covers jumping further, but there is still no month overview.
- "Lo que más trabajas" in `ProgressScreen` was flagged as redundant with Récords in the handoff. Alejandro chose to keep it for now and decide once he has used it.

### Don't infer what the user can just tell you

An earlier version derived the current training "phase" from the date of the oldest logged session. It showed "semana 2 de 3" to someone who had started the day before, because a stray session from testing sat in the database. The lesson stuck: **the active plan is now a value Alejandro sets** (`GymViewModel.activePlan`, backed by SharedPreferences) and the app never guesses it. Counters like `trainedDays` are shown as information, never as rules that drive behaviour.

## Screens

`Hoy` (log today, week strip, gym entry/exit times) · `Máquinas` (92-exercise catalog with search and muscle filters) · `Rutinas` (three collapsible plans → their days) · `Progreso` (profile, body weight chart, stats, share report) · plus `Plan`, `Historial`, `ExerciseDetail`, `RoutineDetail`, `Picker` reached by navigation.

## Testing

`app/src/test/` runs on the JVM: formatting (`FormatTest`), plan integrity (`PlanGuideTest`), report generation (`ReportBuilderTest`). `app/src/androidTest/GymRepositoryTest.kt` exercises the real Room stack on an in-memory database — one session per day, routine loading idempotency, cascade deletes, set renumbering, time handling.

When touching `SeedData.kt`, run `PlanGuideTest` and `GymRepositoryTest`: several tests reference seeded routines by exact name and will catch a rename that would otherwise leave dead buttons in `PlanScreen`.
