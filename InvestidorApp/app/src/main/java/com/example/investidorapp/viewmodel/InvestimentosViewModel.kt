package com.example.investidorapp.viewmodel // Use o seu nome de pacote

import android.Manifest
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import com.example.investidorapp.MainActivity // Use o seu nome de pacote
import com.example.investidorapp.R // Use o seu nome de pacote
import com.example.investidorapp.model.Investimento // Use o seu nome de pacote
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Trocamos para AndroidViewModel para ter acesso ao contexto do aplicativo
class InvestimentosViewModel(application: Application) : AndroidViewModel(application) {
    private val database = Firebase.database.reference.child("investimentos")
    private val _investimentos = MutableStateFlow<List<Investimento>>(emptyList())
    val investimentos: StateFlow<List<Investimento>> = _investimentos

    init {
        monitorarAlteracoes() //
    }

    private fun monitorarAlteracoes() {
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val nome = snapshot.child("nome").getValue(String::class.java) ?: "Desconhecido"
                val valor = snapshot.child("valor").getValue(Long::class.java) ?: 0L
                Log.d("FirebaseData", "Investimento atualizado: $nome R$ $valor")
                // Envia a notificação local
                enviarNotificacao("Investimento Atualizado", "$nome agora vale R$ $valor")
                // Atualiza a lista inteira na UI
                carregarInvestimentos()
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                carregarInvestimentos()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                carregarInvestimentos()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erro ao monitorar alterações: ${error.message}")
            }
        })
    }

    private fun carregarInvestimentos() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = mutableListOf<Investimento>()
                for (item in snapshot.children) {
                    val investimento = item.getValue(Investimento::class.java)
                    if (investimento != null) {
                        lista.add(investimento)
                    }
                }
                _investimentos.value = lista
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Erro ao carregar investimentos: ${error.message}")
            }
        })
    }

    private fun enviarNotificacao(titulo: String, mensagem: String) {
        val context = getApplication<Application>().applicationContext
        val channelId = "Investimentos_notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificações de Investimentos",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 10000).toInt(), builder.build())
        }
    }
}