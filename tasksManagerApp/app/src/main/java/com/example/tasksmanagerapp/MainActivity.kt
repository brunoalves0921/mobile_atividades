package com.example.tasksmanagerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tasksmanagerapp.ui.TasksScreen
import com.example.tasksmanagerapp.ui.theme.TasksManagerAppTheme // Importe seu tema

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TasksManagerAppTheme { // Use o tema do seu projeto
                TasksScreen()
            }
        }
    }
}