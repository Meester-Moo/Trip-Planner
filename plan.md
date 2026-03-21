# ViewModel Implementation Plan

## What is a ViewModel?
ViewModel is an Android Architecture Component that sits between the Activity (View) and the Repository (Model). It holds and manages UI-related data in a lifecycle-aware way, meaning it **survives configuration changes** like screen rotation — unlike Activities which get destroyed and recreated.

## Current Architecture (no ViewModel)
```
Activity  →  Repository  →  DAO  →  Database
```
Activities directly create a Repository and call its methods. This means:
- Data is re-fetched every time the screen rotates
- Business logic lives in the Activity (bad separation of concerns)
- Not scalable as the app grows

## Target Architecture (MVVM with ViewModel)
```
Activity (View)  →  ViewModel  →  Repository  →  DAO  →  Database
```
Activities only talk to their ViewModel. The ViewModel talks to the Repository. This gives us:
- Data survives rotation (no re-fetching)
- Clean separation of concerns
- Scalable, testable architecture

---

## Files to Create (3 new Java files)

### 1. `TripListViewModel.java` (in `database/` package)
- Extends `AndroidViewModel`
- Holds `LiveData<List<Trip>>` for all trips
- Exposes: `getAllTrips()`, `insert(Trip)`

### 2. `TripDetailsViewModel.java` (in `database/` package)
- Extends `AndroidViewModel`
- Exposes: `getAllTrips()`, `getAssociatedExcursions(tripID)`, `insert(Trip)`, `update(Trip)`, `delete(Trip)`, plus excursion CRUD methods

### 3. `ExcursionDetailsViewModel.java` (in `database/` package)
- Extends `AndroidViewModel`
- Exposes: `insert(Excursion, callback)`, `update(Excursion)`, `delete(Excursion)`

---

## Files to Modify

### 4. `libs.versions.toml`
- Add `lifecycle-viewmodel` library entry (uses existing `lifecycle = "2.10.0"` version)

### 5. `app/build.gradle`
- Add `implementation libs.lifecycle.viewmodel` dependency

### 6. `TripList.java`
- Replace direct `Repository` usage with `TripListViewModel`
- Get ViewModel via `new ViewModelProvider(this).get(TripListViewModel.class)`
- Observe `viewModel.getAllTrips()` instead of `repository.getmAllTrips()`

### 7. `TripDetails.java`
- Replace direct `Repository` usage with `TripDetailsViewModel`
- All CRUD calls go through ViewModel

### 8. `ExcursionDetails.java`
- Replace direct `Repository` usage with `ExcursionDetailsViewModel`
- All CRUD calls go through ViewModel

---

## Summary of Changes
- **3 new files** created (ViewModels)
- **2 config files** modified (dependency additions)
- **3 existing files** modified (Activities refactored to use ViewModels)
- **0 files deleted**
- Repository and DAO layers remain unchanged
