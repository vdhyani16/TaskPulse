package com.vikkid.taskpulse.ui.task

import com.vikkid.taskpulse.data.model.TaskItem

sealed interface TaskUiIntent {
    object LoadTasks : TaskUiIntent
    data class MarkCompleted(val taskId: Int) : TaskUiIntent
    data class AddNewTask(val title: String) : TaskUiIntent
}

data class TaskUiState(
    val isLoading: Boolean = false,
    val tasks: List<TaskItem> = emptyList(),
    val errorMessage: String? = null
)

// Single-fire events jaise Snackbar, Navigation (Channel/SharedFlow)
sealed interface TaskUiEffect {
    data class ShowToast(val message: String) : TaskUiEffect
}