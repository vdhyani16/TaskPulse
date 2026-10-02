package com.vikkid.taskpulse.data.repository

import com.vikkid.taskpulse.data.model.Priority
import com.vikkid.taskpulse.data.model.TaskItem
import kotlinx.coroutines.delay

class TaskRepository {
    // Fake remote/local API data source
    suspend fun fetchTasks(): List<TaskItem> {
        delay(1200) // Simulating network latency
        return listOf(
            TaskItem(1, "Master Jetpack Compose Side Effects", isCompleted = true, Priority.HIGH),
            TaskItem(2, "Implement DisposableEffect with NetworkManager", isCompleted = false, Priority.HIGH),
            TaskItem(3, "Refactor State hoisting & derivedStateOf", isCompleted = false, Priority.NORMAL),
            TaskItem(4, "Upload clean code repo to GitHub", isCompleted = false, Priority.LOW)
        )
    }

    suspend fun completeTask(id: Int): Boolean {
        delay(300)
        return true
    }
}