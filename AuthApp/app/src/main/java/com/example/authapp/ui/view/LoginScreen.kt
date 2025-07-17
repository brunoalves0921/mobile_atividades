package com.example.authapp.ui.view

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.authapp.data.AuthRepository
import com.example.authapp.viewmodel.AuthViewModel
import com.example.authapp.viewmodel.AuthViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task

private const val TAG = "LoginScreenDebug"

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val authRepository = AuthRepository()
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(authRepository)
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            Log.d(TAG, "ActivityResultLauncher: Resultado recebido com resultCode: ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.result
                    val idToken = account.idToken
                    Log.d(TAG, "Google Sign-In Account ID Token: $idToken")
                    if (idToken != null) {
                        viewModel.loginWithGoogle(idToken) { success ->
                            if (success) {
                                Log.d(TAG, "Login com Google bem-sucedido, navegando para 'home'.")
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Log.e(TAG, "Falha no login com Google (ViewModel retornou false).")
                                errorMessage = "Falha no login com Google."
                            }
                        }
                    } else {
                        Log.e(TAG, "Token de autenticação com Google não encontrado.")
                        errorMessage = "Token de autenticação com Google não encontrado."
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Login com Google falhou na task: ${e.message}", e)
                    errorMessage = "Login com Google falhou: ${e.message}"
                }
            } else {
                Log.e(TAG, "Login com Google cancelado ou com falha. resultCode: ${result.resultCode}")
                errorMessage = "Login com Google cancelado."
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bem-vindo ao AuthApp!", style = MaterialTheme.typography.titleLarge)
        Text(text = "Login", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedLabelColor = Color.Black,
                unfocusedLabelColor = Color.DarkGray
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
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
                Log.d(TAG, "Botão 'Entrar' clicado. Email: $email")
                viewModel.login(email, password) { success ->
                    if (success) {
                        Log.d(TAG, "Login bem-sucedido, navegando para 'home'.")
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        Log.e(TAG, "Falha no login (ViewModel retornou false).")
                        errorMessage = "Usuário ou senha inválidos."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Entrar")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                Log.d(TAG, "Botão 'Entrar com Google' clicado.")
                val gsc = viewModel.getGoogleSignInClient(context)
                googleSignInLauncher.launch(gsc.signInIntent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("G Entrar com Google")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("register") }) {
            Text("Criar Conta")
        }
        Spacer(modifier = Modifier.height(4.dp))

        TextButton(onClick = { navController.navigate("forgotPassword") }) {
            Text("Esqueci minha senha")
        }

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
    }
}