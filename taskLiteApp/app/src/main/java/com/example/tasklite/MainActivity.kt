package com.example.tasklite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tasklite.ui.TaskListScreen
import com.example.tasklite.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    private val viewModel = TaskViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TaskListScreen(viewModel = viewModel)
        }
    }
}