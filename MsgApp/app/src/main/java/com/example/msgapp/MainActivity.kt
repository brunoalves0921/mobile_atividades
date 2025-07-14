package com.example.msgapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.msgapp.ui.theme.MsgAppTheme
import com.example.msgapp.ui.view.ChatScreen
import com.example.msgapp.ui.view.RoomSelector
import com.example.msgapp.viewmodel.MsgViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            MsgAppTheme {
                MsgAppNavHost()
            }
        }
    }
}

@Composable
fun MsgAppNavHost(vm: MsgViewModel = viewModel()) {
    val navController = rememberNavController()
    val firebaseAuth = remember { FirebaseAuth.getInstance() }
    val user by produceState(initialValue = firebaseAuth.currentUser) {
        if (value == null) {
            firebaseAuth.signInAnonymously().addOnCompleteListener {
                value = firebaseAuth.currentUser
            }
        }
    }
    val userId = user?.uid ?: "Bruno"
    val userName by remember { mutableStateOf(userId) }

    NavHost(navController = navController, startDestination = "room_selector") {
        composable("room_selector") {
            RoomSelector(onRoomSelected = { roomName ->
                // Passa o ID do usuário para o ViewModel saber quem ignorar no "digitando"
                vm.switchRoom(roomName, userId)
                navController.navigate("chat_screen/$roomName")
            })
        }
        composable("chat_screen/{roomName}") { backStackEntry ->
            val roomName = backStackEntry.arguments?.getString("roomName") ?: "geral"
            val messages by vm.messages.collectAsState()
            val typingUsers by vm.typingUsers.collectAsState()

            ChatScreen(
                username = userName,
                userId = userId,
                messages = messages,
                typingUsers = typingUsers,
                onSend = { text, replyTo -> vm.sendMessage(userId, userName, text, replyTo) },
                onSendImage = { uri, replyTo -> vm.sendImage(userId, userName, uri, replyTo) },
                onDelete = { messageId -> vm.deleteMessage(messageId) },
                onUpdateTyping = { isTyping -> vm.updateTypingStatus(userId, userName, isTyping) },
                currentRoom = roomName,
                onLeaveRoom = {
                    vm.updateTypingStatus(userId, userName, false) // Garante que o status seja limpo ao sair
                    navController.popBackStack()
                }
            )
        }
    }
}