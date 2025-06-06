package com.example.tasksmanagerapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasksmanagerapp.data.Task
import com.example.tasksmanagerapp.data.TaskCategory
import com.example.tasksmanagerapp.data.TaskPriority
import com.example.tasksmanagerapp.datastore.DataStoreUtils
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TasksViewModel(context: Context) : ViewModel() {

    private val _tasks = MutableStateFlow(
        listOf(
            Task("Reunião importante", false, TaskCategory.TRABALHO, TaskPriority.ALTA),
            Task("Estudar Jetpack Compose", false, TaskCategory.ESTUDOS, TaskPriority.MEDIA),
            Task("Limpar a casa", false, TaskCategory.CASA, TaskPriority.BAIXA)
        )
    )
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // Canal para enviar mensagens para a UI
    private val _toastChannel = Channel<String>()
    val toastFlow = _toastChannel.receiveAsFlow()

    private var lastDeletedTask: Task? = null

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            DataStoreUtils.readTheme(context).collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }
        updateProgress()
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            // Verifica se a tarefa já existe
            if (_tasks.value.any { it.name.equals(task.name, ignoreCase = true) }) {
                _toastChannel.send("Uma tarefa com este nome já existe.")
            } else {
                _tasks.value = _tasks.value + task
                updateProgress()
            }
        }
    }

    fun removeTask(task: Task) {
        lastDeletedTask = task
        _tasks.value = _tasks.value.filter { it.id != task.id } // Usar ID para remover
        updateProgress()
    }

    fun undoDelete() {
        lastDeletedTask?.let {
            _tasks.value = _tasks.value + it
            lastDeletedTask = null
            updateProgress()
        }
    }

    fun toggleTaskCompletion(task: Task) {
        _tasks.value = _tasks.value.map {
            if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it // Usar ID para atualizar
        }
        updateProgress()
    }

    private fun updateProgress() {
        val completed = _tasks.value.count { it.isCompleted }
        _progress.value = if (_tasks.value.isNotEmpty()) completed.toFloat() / _tasks.value.size else 0f
    }

    fun toggleTheme(context: Context) {
        viewModelScope.launch {
            val newTheme = !_isDarkTheme.value
            _isDarkTheme.value = newTheme
            DataStoreUtils.saveTheme(context, newTheme)
        }
    }
}