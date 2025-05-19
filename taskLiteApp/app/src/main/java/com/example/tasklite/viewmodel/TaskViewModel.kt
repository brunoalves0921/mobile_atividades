package com.example.tasklite.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tasklite.model.Task
import com.example.tasklite.model.TaskFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class TaskViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    val filteredTasks: StateFlow<List<Task>> = combine(_tasks, _filter) { tasks, filter ->
        when (filter) {
            TaskFilter.ALL -> tasks
            TaskFilter.COMPLETED -> tasks.filter { it.isDone }
            TaskFilter.PENDING -> tasks.filter { !it.isDone }
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    private var nextId = 1

    fun onAddTask(title: String) {
        if (title.isNotBlank()) {
            val newTask = Task(id = nextId++, title = title)
            _tasks.value = _tasks.value + newTask
        }
    }

    fun onTaskChecked(id: Int) {
        _tasks.value = _tasks.value.map {
            if (it.id == id) it.copy(isDone = !it.isDone) else it
        }
    }

    fun onDeleteTask(id: Int) {
        _tasks.value = _tasks.value.filterNot { it.id == id }
    }

    fun onFilterSelected(filter: TaskFilter) {
        _filter.value = filter
    }
}