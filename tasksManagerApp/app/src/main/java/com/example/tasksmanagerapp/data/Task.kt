package com.example.tasksmanagerapp.data

import java.util.UUID

enum class TaskPriority { BAIXA, MEDIA, ALTA }
enum class TaskCategory { TRABALHO, CASA, ESTUDOS }

data class Task(
    val name: String,
    val isCompleted: Boolean = false,
    val category: TaskCategory = TaskCategory.CASA,
    val priority: TaskPriority = TaskPriority.MEDIA,
    // Adicione esta linha para o ID único
    val id: UUID = UUID.randomUUID()
)