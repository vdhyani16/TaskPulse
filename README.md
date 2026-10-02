# TaskPulse ⚡

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Material3](https://img.shields.io/badge/Design-Material%203-757575.svg?style=flat)](https://m3.material.io/)

**TaskPulse** is a clean, modern Android application demonstrating **Model-View-Intent (MVI)** architecture, **Unidirectional Data Flow (UDF)**, and advanced **Jetpack Compose** state management and side-effects.

---

## 📌 Repository Description (For GitHub About Section)

> **Short Description / About:**
> Modern Android task management app built with Jetpack Compose, Material 3, and MVI architecture showcasing Kotlin Coroutines, StateFlow, Channels, and lifecycle-aware side-effects.

> **Topics / Tags:**
> `android`, `kotlin`, `jetpack-compose`, `mvi-architecture`, `material3`, `coroutines`, `stateflow`, `android-development`

---

## ✨ Features

- **Task Management**: Real-time task creation and completion toggle.
- **Dynamic Metrics**: Computed task statistics using `derivedStateOf`.
- **Real-Time Connectivity Monitoring**: Live network status indicator banner with `DisposableEffect` system listener.
- **Single-Event Handling**: Toast and Snackbar effects via Kotlin `Channel` streams.
- **Material 3 Theming**: Responsive layout using Material 3 surfaces, cards, and dynamic badges.

---

## 🏛️ Architecture (MVI + UDF)

The application follows the **Model-View-Intent (MVI)** pattern:

```
                  ┌─────────────────┐
                  │   TaskUiIntent  │ (User Actions)
                  └────────┬────────┘
                           │
                           ▼
                  ┌─────────────────┐
                  │  TaskViewModel  │
                  └────────┬────────┘
                           │
       ┌───────────────────┴───────────────────┐
       ▼                                       ▼
┌──────────────┐                       ┌──────────────┐
│ TaskUiState  │ (StateFlow)           │ TaskUiEffect │ (Channel)
└──────┬───────┘                       └──────┬───────┘
       │                                      │
       ▼                                      ▼
┌──────────────┐                       ┌──────────────┐
│  TaskScreen  │                       │   Snackbar   │
└──────────────┘                       └──────────────┘
```

- **`TaskUiIntent`**: Defines user actions (`LoadTasks`, `MarkCompleted`, `AddNewTask`).
- **`TaskUiState`**: Immutable state snapshot containing `isLoading`, `tasks`, and `errorMessage`.
- **`TaskUiEffect`**: Single-fire UI side-effects (e.g., `ShowToast`).

---

## 💡 Key Jetpack Compose Concepts

| Concept | Usage in TaskPulse |
| :--- | :--- |
| `DisposableEffect` | Lifecycle-aware registration and cleanup of `ConnectivityManager.NetworkCallback` with zero memory leaks. |
| `LaunchedEffect` | Initial load triggers and side-effect flow collection tied to composable lifecycle. |
| `derivedStateOf` | Optimized recalculation of completed vs. total task counts. |
| `collectAsStateWithLifecycle` | Lifecycle-aware observation of `StateFlow` state streams. |
| `LazyColumn` Keys | Optimized list rendering using unique task IDs to avoid unnecessary recompositions. |

---

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose & Material 3
- **Asynchronous & Reactive**: Kotlin Coroutines, `StateFlow`, `Channel`
- **Architecture**: MVI, ViewModel
- **Build System**: Gradle Version Catalog (`libs.versions.toml`)

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/vikkid/TaskPulse.git
   ```
2. Open the project in **Android Studio (Ladybug or newer)**.
3. Build and run on an emulator or physical device running **Android 7.0 (API level 24)+**.

---

## 📄 License

```
Copyright (c) 2026 Vikas

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
