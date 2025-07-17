package com.example.authapp.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.authapp.data.AuthRepository
import com.example.authapp.viewmodel.AuthViewModel
import com.example.authapp.viewmodel.AuthViewModelFactory

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val authRepository = AuthRepository()
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Recuperar Senha", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Digite seu email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.DarkGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotEmpty()) {
                    viewModel.resetPassword(email) { success ->
                        message = if (success) {
                            "Email de recuperação enviado com sucesso."
                        } else {
                            "Erro ao enviar email. Verifique o endereço e tente novamente."
                        }
                    }
                } else {
                    message = "Por favor, digite seu email."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Email de Recuperação")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Voltar ao Login")
        }

        message?.let {
            Text(text = it, modifier = Modifier.padding(top = 8.dp), color = if (it.startsWith("Erro")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }
    }
}