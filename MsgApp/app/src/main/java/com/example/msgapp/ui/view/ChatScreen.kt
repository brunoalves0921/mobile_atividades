package com.example.msgapp.ui.view

import android.text.format.DateFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.msgapp.model.Message

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    username: String,
    userId: String,
    messages: List<Message>,
    typingUsers: List<String>,
    onSend: (String, Message?) -> Unit,
    onDelete: (String) -> Unit,
    onUpdateTyping: (Boolean) -> Unit,
    currentRoom: String,
    onLeaveRoom: (() -> Unit)? = null
) {
    var input by remember { mutableStateOf("") }
    var messageToReply by remember { mutableStateOf<Message?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var messageToAction by remember { mutableStateOf<Message?>(null) }

    val listState = rememberLazyListState()

    // Lógica para o indicador "digitando"
    LaunchedEffect(input) {
        onUpdateTyping(input.isNotBlank())
    }

    // Rola para a última mensagem quando a lista é atualizada
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(Modifier.fillMaxSize()) {
        // Cabeçalho
        Surface(
            tonalElevation = 2.dp, shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sala: $currentRoom", style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                if (onLeaveRoom != null) {
                    TextButton(onClick = { onLeaveRoom() }) {
                        Text("Sair da sala", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }

        // Lista de Mensagens
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    MessageBubble(
                        msg = msg,
                        isOwn = msg.senderId == userId,
                        onLongPress = {
                            messageToAction = msg
                            showMenu = true
                        }
                    )
                }
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Responder") }, onClick = {
                    messageToReply = messageToAction
                    showMenu = false
                })
                if (messageToAction?.senderId == userId) {
                    DropdownMenuItem(text = { Text("Apagar") }, onClick = {
                        messageToAction?.let { onDelete(it.id) }
                        showMenu = false
                    })
                }
            }
        }

        // Rodapé com campo de entrada
        Column {
            if (typingUsers.isNotEmpty()) {
                val typingText = when (typingUsers.size) {
                    1 -> "${typingUsers.first()} está digitando..."
                    else -> "${typingUsers.size} usuários estão digitando..."
                }
                Text(
                    text = typingText,
                    style = MaterialTheme.typography.labelMedium, fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
            }

            Surface(tonalElevation = 2.dp, shadowElevation = 4.dp) {
                Column {
                    if (messageToReply != null) {
                        ReplyPreview(message = messageToReply!!, onCancel = { messageToReply = null })
                    }
                    Row(
                        Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // O botão de anexo foi removido daqui
                        OutlinedTextField(
                            value = input, onValueChange = { input = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Digite sua mensagem...") },
                            shape = RoundedCornerShape(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Button(
                            onClick = {
                                if (input.isNotBlank()) {
                                    onSend(input, messageToReply)
                                    input = ""
                                    messageToReply = null
                                }
                            },
                            shape = CircleShape, enabled = input.isNotBlank(),
                            modifier = Modifier.size(44.dp), contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("➤", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(msg: Message, isOwn: Boolean, onLongPress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isOwn) {
            Avatar(name = msg.senderName)
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            color = if (isOwn) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = if (isOwn) 20.dp else 4.dp,
                topEnd = if (isOwn) 4.dp else 20.dp,
                bottomEnd = 20.dp,
                bottomStart = 20.dp
            ),
            shadowElevation = 1.dp,
            modifier = Modifier
                .widthIn(max = 300.dp)
                .combinedClickable(
                    onClick = { /* Ação de clique simples (opcional) */ },
                    onLongClick = onLongPress
                )
        ) {
            Column(
                modifier = Modifier.padding(
                    start = 14.dp,
                    end = 14.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
            ) {
                msg.replyTo?.let { replyMsg ->
                    ReplyMessageContent(replyMsg)
                }

                // A lógica 'when' foi removida. Exibimos apenas o texto.
                Text(
                    text = msg.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isOwn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = DateFormat.format("HH:mm", msg.timeStamp).toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = (if (isOwn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface).copy(
                        alpha = 0.7f
                    ),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ReplyPreview(message: Message, onCancel: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
    ) {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Respondendo a ${message.senderName}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp
                )
                // Mostra apenas o texto da mensagem respondida
                Text(
                    text = message.text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onCancel, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Cancelar resposta")
            }
        }
    }
}

@Composable
fun ReplyMessageContent(message: Message) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f)
    ) {
        Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Column {
                Text(
                    text = message.senderName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                // Mostra apenas o texto da mensagem respondida
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = FontStyle.Italic,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun Avatar(name: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.firstOrNull()?.uppercase() ?: "",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}