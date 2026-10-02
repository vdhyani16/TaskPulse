package com.vikkid.taskpulse.data.model

import androidx.compose.runtime.Immutable

@Immutable
data class TaskItem(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.NORMAL
)

enum class Priority { LOW, NORMAL, HIGH }