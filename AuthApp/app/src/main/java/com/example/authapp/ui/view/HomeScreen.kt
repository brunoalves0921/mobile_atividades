package com.example.authapp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.authapp.data.AuthRepository
import com.example.authapp.viewmodel.AuthViewModel
import com.example.authapp.viewmodel.AuthViewModelFactory

@Composable
fun HomeScreen(navController: NavController) {
    val authRepository = AuthRepository()
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    var userName by remember { mutableStateOf("Carregando...") }

    LaunchedEffect(Unit) {
        viewModel.getUserName { name ->
            userName = name ?: "Usuário"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bem-vindo, $userName!",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sair")
        }
    }
}