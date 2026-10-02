package com.vikkid.taskpulse.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikkid.taskpulse.data.model.Priority
import com.vikkid.taskpulse.data.model.TaskItem
import com.vikkid.taskpulse.data.repository.TaskRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository = TaskRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    // Side-effects / One-time events channel
    private val _effectChannel = Channel<TaskUiEffect>()
    val effect = _effectChannel.receiveAsFlow()

    fun processIntent(intent: TaskUiIntent) {
        when (intent) {
            is TaskUiIntent.LoadTasks -> loadTasks()
            is TaskUiIntent.MarkCompleted -> toggleTask(intent.taskId)
            is TaskUiIntent.AddNewTask -> addTask(intent.title)
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val data = repository.fetchTasks()
                _uiState.update { it.copy(isLoading = false, tasks = data) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load tasks") }
            }
        }
    }

    private fun toggleTask(id: Int) {
        viewModelScope.launch {
            repository.completeTask(id)
            _uiState.update { current ->
                current.copy(tasks = current.tasks.map {
                    if (it.id == id) it.copy(isCompleted = !it.isCompleted) else it
                })
            }
            _effectChannel.send(TaskUiEffect.ShowToast("Task status updated!"))
        }
    }

    private fun addTask(title: String) {
        if (title.isBlank()) return
        val newTask = TaskItem(
            id = (System.currentTimeMillis() % 10000).toInt(),
            title = title,
            isCompleted = false,
            priority = Priority.NORMAL
        )
        _uiState.update { it.copy(tasks = listOf(newTask) + it.tasks) }
    }
}