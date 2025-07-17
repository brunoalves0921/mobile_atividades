package com.example.authapp.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.example.authapp.data.AuthRepository
import kotlinx.coroutines.launch

private const val TAG = "AuthViewModelDebug"

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Recebendo pedido de login.")
        viewModelScope.launch {
            val success = repository.loginUser(email, password)
            Log.d(TAG, "Resultado do login: $success")
            onResult(success)
        }
    }

    fun resetPassword(email: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Recebendo pedido de recuperação de senha.")
        viewModelScope.launch {
            val success = repository.resetPassword(email)
            Log.d(TAG, "Resultado da recuperação de senha: $success")
            onResult(success)
        }
    }

    fun getUserName(onResult: (String?) -> Unit) {
        Log.d(TAG, "Recebendo pedido para buscar nome do usuário.")
        viewModelScope.launch {
            val name = repository.getUserName()
            Log.d(TAG, "Nome do usuário recebido: $name")
            onResult(name)
        }
    }

    fun loginWithGoogle(idToken: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Recebendo pedido de login com Google.")
        viewModelScope.launch {
            val success = repository.loginWithGoogle(idToken)
            Log.d(TAG, "Resultado do login com Google: $success")
            onResult(success)
        }
    }

    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        return repository.getGoogleSignInClient(context)
    }

    fun logout() {
        Log.d(TAG, "Chamando logout.")
        repository.logout()
    }

    fun register(email: String, password: String, name: String, onResult: (Boolean) -> Unit) {
        Log.d(TAG, "Recebendo pedido de registro.")
        viewModelScope.launch {
            val success = repository.registerUser(email, password, name)
            Log.d(TAG, "Resultado do registro: $success")
            onResult(success)
        }
    }
}