package com.vikkid.taskpulse.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vikkid.taskpulse.data.model.TaskItem
import com.vikkid.taskpulse.ui.components.NetworkStatusTracker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun TaskScreen(
    viewModel: TaskViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // 2. REMEMBER COROUTINE SCOPE: Button taps ke user events ke liye
    val coroutineScope = rememberCoroutineScope()

    // 3. LAUNCHED EFFECT (One-time Initial API load)
    LaunchedEffect(Unit) {
        viewModel.processIntent(TaskUiIntent.LoadTasks)
    }

    // 4. LAUNCHED EFFECT (Observing ViewModel Effect Channel safely)
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is TaskUiEffect.ShowToast -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 5. DERIVED STATE OF: Expensive calculations ko cache karna
    // List me kitne tasks complete huye hain, ye tabhi calculate hoga jab tasks list update hogi
    val completedCount by remember {
        derivedStateOf { uiState.tasks.count { it.isCompleted } }
    }

    var textInput by remember { mutableStateOf("") }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column {
                NetworkStatusTracker()
                Surface(shadowElevation = 3.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Task Pulse",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Badge {
                            Text("Done: $completedCount/${uiState.tasks.size}")
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Task Input Area
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    label = { Text("New task title") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        viewModel.processIntent(TaskUiIntent.AddNewTask(textInput))
                        textInput = ""
                        // Coroutine scope se snackbar manual user gesture par show karna
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Task queued locally!")
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Task")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Body Content with State checks
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.tasks,
                            key = { it.id } // Performance key: LazyColumn recomposition skip karega
                        ) { task ->
                            TaskRowItem(
                                task = task,
                                onToggle = {
                                    viewModel.processIntent(TaskUiIntent.MarkCompleted(task.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// 6. STATE HOISTING: Reusable & Stateless Component
@Composable
fun TaskRowItem(
    task: TaskItem,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) Color.Gray else Color.Unspecified
                )
                Text(
                    text = "Priority: ${task.priority.name}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(onClick = onToggle) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Toggle Complete",
                    tint = if (task.isCompleted) Color(0xFF4CAF50) else Color.LightGray
                )
            }
        }
    }
}