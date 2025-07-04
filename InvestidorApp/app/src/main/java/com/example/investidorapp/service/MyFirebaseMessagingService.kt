package com.example.investidorapp.service // Use o seu nome de pacote

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.investidorapp.MainActivity // Use o seu nome de pacote
import com.example.investidorapp.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() { // [cite: 900]

    override fun onMessageReceived(remoteMessage: RemoteMessage) { // [cite: 902]
        super.onMessageReceived(remoteMessage)
        remoteMessage.notification?.let {
            showNotification(it.title, it.body) // [cite: 904]
        }
    }

    override fun onNewToken(token: String) { // [cite: 906]
        super.onNewToken(token)
        println("Token do dispositivo: $token") // [cite: 908]
    }

    private fun showNotification(title: String?, message: String?) { // [cite: 910]
        val channelId = "investidor_notifications" // [cite: 911]
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // [cite: 913]
            val channel = NotificationChannel( // [cite: 914]
                channelId,
                "Notificações de Investimentos", // [cite: 920]
                NotificationManager.IMPORTANCE_HIGH // [cite: 922]
            )
            val manager = getSystemService(NotificationManager::class.java) // [cite: 923, 924]
            manager.createNotificationChannel(channel) // [cite: 925]
        }

        val intent = Intent(this, MainActivity::class.java).apply { // [cite: 926]
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // [cite: 927]
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT) // [cite: 929, 930]

        val builder = NotificationCompat.Builder(this, channelId) // [cite: 932]
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title ?: "Nova Notificação") // [cite: 934]
        .setContentText(message ?: "Você recebeu uma nova mensagem.")
        .setPriority(NotificationCompat.PRIORITY_HIGH) // [cite: 936]
        .setContentIntent(pendingIntent) // [cite: 937]
        .setAutoCancel(true) // [cite: 938]

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) { // [cite: 940, 941, 942, 943]
            return // [cite: 944]
        }
        NotificationManagerCompat.from(this).notify((System.currentTimeMillis() % 10000).toInt(), builder.build()) // [cite: 947]
    }
}