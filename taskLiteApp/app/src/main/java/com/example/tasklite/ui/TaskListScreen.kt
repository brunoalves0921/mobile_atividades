package com.example.tasklite.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasklite.viewmodel.TaskViewModel
import com.example.tasklite.model.TaskFilter

@Composable
fun TaskListScreen(viewModel: TaskViewModel, modifier: Modifier = Modifier) {
    val taskList by viewModel.filteredTasks.collectAsState()
    var newTaskTitle by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("TaskLite (Desafio)", fontSize = 24.sp)
        Spacer(Modifier.height(16.dp))

        Row {
            Button(onClick = { viewModel.onFilterSelected(TaskFilter.ALL) }) {
                Text("Todas")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { viewModel.onFilterSelected(TaskFilter.PENDING) }) {
                Text("Pendentes")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = { viewModel.onFilterSelected(TaskFilter.COMPLETED) }) {
                Text("Concluídas")
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(taskList) { task ->
                TaskItem(
                    task = task,
                    onCheckedChange = { viewModel.onTaskChecked(task.id) },
                    onDelete = { viewModel.onDeleteTask(task.id) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row {
            TextField(
                value = newTaskTitle,
                onValueChange = { newTaskTitle = it },
                label = { Text("Nova tarefa") },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                viewModel.onAddTask(newTaskTitle)
                newTaskTitle = ""
            }) {
                Text("Adicionar")
            }
        }
    }
}