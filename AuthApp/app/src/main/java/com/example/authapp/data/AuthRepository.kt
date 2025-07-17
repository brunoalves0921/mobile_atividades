package com.example.authapp.data

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.authapp.R

private const val TAG = "AuthRepositoryDebug"

class AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Registro de usuário
    suspend fun registerUser(email: String, password: String, name: String): Boolean {
        Log.d(TAG, "Iniciando registro para o email: $email")
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid
            Log.d(TAG, "Usuário registrado com sucesso. UID: $uid")
            if (uid != null) {
                val user = hashMapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email,
                    "created at" to System.currentTimeMillis()
                )
                firestore.collection("users").document(uid).set(user).await()
                Log.d(TAG, "Dados do usuário salvos no Firestore.")
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro no cadastro: ${e.message}", e)
            false
        }
    }

    // Login com email e senha
    suspend fun loginUser(email: String, password: String): Boolean {
        Log.d(TAG, "Iniciando login para o email: $email")
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Log.d(TAG, "Login com email/senha bem-sucedido.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro no login: ${e.message}", e)
            false
        }
    }

    // Recuperação de senha
    suspend fun resetPassword(email: String): Boolean {
        Log.d(TAG, "Iniciando recuperação de senha para: $email")
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "Email de recuperação enviado com sucesso.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao enviar email de recuperação: ${e.message}", e)
            false
        }
    }

    // Obter nome do usuário
    suspend fun getUserName(): String? {
        Log.d(TAG, "Buscando nome do usuário no Firestore.")
        return try {
            val uid = auth.currentUser?.uid
            if (uid != null) {
                val snapshot = firestore.collection("users").document(uid).get().await()
                val name = snapshot.getString("name")
                Log.d(TAG, "Nome do usuário encontrado: $name")
                name
            } else {
                Log.d(TAG, "Usuário não logado, não é possível buscar o nome.")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar nome do usuário: ${e.message}", e)
            null
        }
    }

    // Login com Google
    fun getGoogleSignInClient(context: Context): GoogleSignInClient {
        Log.d(TAG, "Obtendo GoogleSignInClient.")
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun loginWithGoogle(idToken: String): Boolean {
        Log.d(TAG, "Iniciando login com Google, ID Token: $idToken")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user
            Log.d(TAG, "Login com Google bem-sucedido. UID: ${user?.uid}")
            user?.let {
                val uid = it.uid
                val name = it.displayName ?: "Usuario"
                val email = it.email ?: ""
                val userRef = firestore.collection("users").document(uid)
                val snapshot = userRef.get().await()
                if (!snapshot.exists()) {
                    Log.d(TAG, "Usuário Google novo, salvando dados no Firestore.")
                    val userData = hashMapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email,
                        "created at" to System.currentTimeMillis()
                    )
                    userRef.set(userData).await()
                    Log.d(TAG, "Dados do usuário Google salvos.")
                } else {
                    Log.d(TAG, "Usuário Google já existe no Firestore, pulando o salvamento.")
                }
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Erro no login Google: ${e.message}", e)
            false
        }
    }

    // Logout
    fun logout() {
        Log.d(TAG, "Realizando logout.")
        auth.signOut()
    }

    // Verifica se o usuário está logado
    fun isUserLogged(): Boolean {
        return auth.currentUser != null
    }
}