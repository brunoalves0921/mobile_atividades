package com.example.nighteventsapp.ui.screens

// Imports adicionados para a lógica de permissão
import android.Manifest // Mantenha este import
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager // Mantenha este import
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult // Mantenha este import
import androidx.activity.result.contract.ActivityResultContracts // Mantenha este import
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Adicionado para obter contexto no launcher se necessário
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat // Mantenha este import
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat // Mantenha este import
import androidx.navigation.NavHostController
import com.example.nighteventsapp.models.eventList
// Se sua classe Event estiver em R.drawable, certifique-se que o import de R está correto
// import com.example.nighteventsapp.R // Exemplo

@Composable
fun HomeScreen (navController : NavHostController) { // Removido o parâmetro context daqui
    val context = LocalContext.current // Obtém o contexto aqui

    // Launcher para solicitar a permissão de notificação
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Permissão concedida.
                // Aqui você pode querer reenviar a notificação se o evento ainda estiver
                // marcado para ser notificado e a notificação não foi enviada antes.
                // Para simplificar, assumimos que a ação de inscrever já foi registrada
                // e a notificação será (ou foi) enviada na lógica do botão.
            } else {
                // Permissão negada. Você pode mostrar uma mensagem para o usuário.
                // Ex: Toast.makeText(context, "Permissão de notificação negada.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Cria o canal de notificação uma vez quando o Composable entra na composição
    LaunchedEffect(Unit) {
        createNotificationChannel(context)
    }

    Column {
        val subscribedEvents = eventList.filter { it.isSubscribed.value }
        if (subscribedEvents.isNotEmpty()) {
            Text(
                text = "Eventos Inscritos",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp)
            )
            LazyRow (
                modifier = Modifier.padding(horizontal = 16.dp), // Apenas padding horizontal para LazyRow
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subscribedEvents) { event ->
                    Card(
                        modifier = Modifier
                            .size(60.dp)
                            .clickable {
                                navController.navigate("eventDetails/${event.id}")
                            },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = event.imageRes),
                            contentDescription = event.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn (
            modifier = Modifier.padding(horizontal = 16.dp) // Apenas padding horizontal
        ) {
            items(eventList) { event ->
                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth() // Garante que o card ocupe a largura
                        .clickable {
                            navController.navigate("eventDetails/${event.id}")
                        },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image (
                                painter = painterResource(id = event.imageRes),
                                contentDescription = event.title,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = event.location,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Icon(
                                imageVector = if (event.isFavorite.value) Icons.Default.Favorite else
                                    Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable {
                                        event.isFavorite.value = !event.isFavorite.value
                                    }
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = event.description,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                // Alterna o estado de inscrição primeiro
                                val wasSubscribed = event.isSubscribed.value
                                event.isSubscribed.value = !event.isSubscribed.value

                                // Se o usuário está SE INSCREVENDO
                                if (event.isSubscribed.value) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        when {
                                            ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.POST_NOTIFICATIONS
                                            ) == PackageManager.PERMISSION_GRANTED -> {
                                                // Permissão já concedida
                                                sendNotification(context, event.title)
                                            }
                                            // Opcional: Mostrar justificativa se negou anteriormente
                                            // ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.POST_NOTIFICATIONS) -> { ... }
                                            else -> {
                                                // Solicitar permissão. A notificação será (ou não) enviada
                                                // com base na resposta do usuário no callback do launcher.
                                                // Se você quiser enviar a notificação após a permissão ser concedida
                                                // pelo launcher, você precisaria de uma lógica mais elaborada
                                                // (ex: um estado para lembrar qual evento acionou o pedido).
                                                // Por simplicidade aqui, pedimos a permissão. Se concedida,
                                                // o usuário teria que clicar novamente para enviar se a notificação
                                                // só é enviada no bloco 'PERMISSION_GRANTED'.
                                                // Ou, modificar o callback do launcher para enviar a notificação
                                                // para um evento específico se a permissão for concedida lá.

                                                // Para este exemplo, vamos tentar enviar após a solicitação,
                                                // e a função sendNotification fará a checagem final.
                                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                // Re-verificar após o launcher (embora o launcher seja assíncrono)
                                                // Uma melhor abordagem seria gerenciar o estado da notificação pendente.
                                                // Vamos simplificar: se a permissão for concedida no launcher,
                                                // o usuário precisaria interagir novamente para a notificação ser enviada,
                                                // ou você precisaria de uma lógica de estado mais complexa.
                                                // Vamos assumir que se o launcher for chamado, a notificação será
                                                // tentada (e sendNotification fará a checagem).
                                                // E se a permissão FOR concedida via launcher, a PRÓXIMA vez que ele clicar, funcionará.

                                                // Uma forma de fazer a notificação ser enviada após a permissão é
                                                // ter o launcher modificar um estado que este botão observa.
                                                // Por agora, vamos manter a chamada a sendNotification e deixar ela checar.
                                                if (ContextCompat.checkSelfPermission(
                                                        context,
                                                        Manifest.permission.POST_NOTIFICATIONS
                                                    ) == PackageManager.PERMISSION_GRANTED) {
                                                    sendNotification(context, event.title)
                                                }

                                            }
                                        }
                                    } else {
                                        // Em versões anteriores ao Android 13
                                        sendNotification(context, event.title)
                                    }
                                }
                                // Se estava inscrito e agora não está (isSubscribed.value é false),
                                // nenhuma notificação de "inscrição" é enviada.
                                // Poderia-se cancelar uma notificação existente aqui, se relevante.
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (event.isSubscribed.value) "Inscrito" else "Se Inscrever"
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                navController.navigate("eventDetails/${event.id}") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Ver mais sobre ${event.title}") // Adicionado espaço
                        }
                    }
                }
            }
        }
    }
}

// Função para criar o canal de notificação
private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Inscrição de Eventos"
        val descriptionText = "Canal para notificações de inscrição em eventos"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channelId = "EVENT_CHANNEL" // Mantém o ID do canal consistente
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

// Função para enviar a notificação (com verificação de permissão como salvaguarda)
private fun sendNotification(context: Context, eventTitle: String) {
    val channelId = "EVENT_CHANNEL" // Deve ser o mesmo ID usado em createNotificationChannel

    // Verificação de permissão (Boa prática, embora a lógica de solicitação principal esteja no Composable)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
    ) {
        // Log.w("Notification", "Permissão POST_NOTIFICATIONS não concedida. A notificação para '$eventTitle' não será enviada.")
        // Idealmente, a UI já tratou disso ou o usuário foi informado.
        return // Não prossegue para enviar a notificação
    }

    val builder = NotificationCompat.Builder(context, channelId)
        // **IMPORTANTE: Substitua pelo seu ícone de notificação!**
        // Ex: R.drawable.ic_stat_your_app_icon (deve ser branco com transparência)
        .setSmallIcon(android.R.drawable.ic_notification_overlay)
        .setContentTitle("Inscrição Confirmada")
        .setContentText("Você foi inscrito no evento: $eventTitle")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true) // Opcional: remove a notificação ao ser tocada

    with(NotificationManagerCompat.from(context)) {
        // O ID da notificação deve ser único para cada notificação que você quer que seja distinta
        try {
            notify(eventTitle.hashCode(), builder.build())
        } catch (e: SecurityException) {
            // Log.e("Notification", "SecurityException ao enviar notificação para '$eventTitle': ${e.message}")
        }
    }
}