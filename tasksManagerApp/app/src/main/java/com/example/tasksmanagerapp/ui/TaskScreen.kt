package com.example.tasksmanagerapp.ui

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasksmanagerapp.data.Task
import com.example.tasksmanagerapp.data.TaskCategory
import com.example.tasksmanagerapp.data.TaskPriority
import com.example.tasksmanagerapp.viewmodel.TasksViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(vm: TasksViewModel = viewModel(factory = TasksViewModelFactory(LocalContext.current))) {
    val tasks by vm.tasks.collectAsState()
    val progress by vm.progress.collectAsState()
    val isDarkTheme by vm.isDarkTheme.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // **A CORREÇÃO ESTÁ AQUI**
    // 1. O contexto é capturado uma única vez dentro do escopo do @Composable.
    val context = LocalContext.current

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "ProgressAnimation",
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )

    // Efeito para escutar as mensagens do ViewModel e exibir na Snackbar
    LaunchedEffect(Unit) {
        vm.toastFlow.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Gerenciador de Tarefas") },
                    actions = {
                        // 2. A variável 'context' é usada aqui, resolvendo o erro.
                        IconButton(onClick = { vm.toggleTheme(context) }) {
                            Icon(Icons.Default.BrightnessHigh, contentDescription = "Alternar Tema")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text("Progresso das Tarefas")
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Barra de progresso customizada, suave e sem bugs visuais
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction = animatedProgress)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    val emoji = when {
                        progress == 1f -> "🎉"
                        progress >= 0.8f -> "😊"
                        progress >= 0.4f -> "😐"
                        else -> "😞"
                    }
                    Text(text = emoji, style = MaterialTheme.typography.headlineSmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Usando o 'task.id' como chave para evitar crashes com nomes duplicados
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggleCompletion = { vm.toggleTaskCompletion(task) },
                            onDelete = {
                                vm.removeTask(task)
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Tarefa removida",
                                        actionLabel = "Desfazer",
                                        duration = SnackbarDuration.Short
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        vm.undoDelete()
                                    }
                                }
                            }
                        )
                    }
                }
                AddTaskSection { name, category, priority ->
                    vm.addTask(Task(name = name, category = category, priority = priority))
                }
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggleCompletion: () -> Unit, onDelete: () -> Unit) {
    val backgroundColor = when (task.priority) {
        TaskPriority.BAIXA -> Color(0xFFC8E6C9)
        TaskPriority.MEDIA -> Color(0xFFFFF59D)
        TaskPriority.ALTA -> Color(0xFFFFCDD2)
    }

    AnimatedVisibility(visible = true) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .background(backgroundColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleCompletion() }
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = task.name,
                    color = if (task.isCompleted) Color.Gray else Color.Black,
                    style = if (task.isCompleted) MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = TextDecoration.LineThrough
                    ) else MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = task.category.name,
                    color = if (task.isCompleted) Color.Gray else Color.DarkGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Deletar Tarefa",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun AddTaskSection(onAddTask: (String, TaskCategory, TaskPriority) -> Unit) {
    var taskName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TaskCategory.CASA) }
    var selectedPriority by remember { mutableStateOf(TaskPriority.MEDIA) }

    Column {
        OutlinedTextField(
            value = taskName,
            onValueChange = { taskName = it },
            label = { Text("Nova tarefa") },
            modifier = Modifier.fillMaxWidth(),
            isError = false
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropdownMenuBox("Categoria", TaskCategory.values().map { it.name }, selectedCategory.name) {
                selectedCategory = TaskCategory.valueOf(it)
            }
            DropdownMenuBox("Prioridade", TaskPriority.values().map { it.name }, selectedPriority.name) {
                selectedPriority = TaskPriority.valueOf(it)
            }
        }
        Button(
            onClick = {
                if (taskName.isNotBlank()) {
                    onAddTask(taskName, selectedCategory, selectedPriority)
                    taskName = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Adicionar Tarefa")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RowScope.DropdownMenuBox(label: String, options: List<String>, initialSelection: String, onSelection: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(initialSelection) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .padding(4.dp)
            .weight(1f)
    ) {
        OutlinedButton(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            onClick = { expanded = true }
        ) {
            Text("$label: $selectedOption")
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        onSelection(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

class TasksViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}