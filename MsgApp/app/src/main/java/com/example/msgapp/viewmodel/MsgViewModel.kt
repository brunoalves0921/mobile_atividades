package com.example.msgapp.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.example.msgapp.model.Message
import com.example.msgapp.model.MessageType
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class MsgViewModel(application: Application) : AndroidViewModel(application) {

    private var currentRoom: String = "geral"
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // Novo StateFlow para o indicador "digitando"
    private val _typingUsers = MutableStateFlow<List<String>>(emptyList())
    val typingUsers: StateFlow<List<String>> = _typingUsers

    private var messageListener: ValueEventListener? = null
    private var typingListener: ValueEventListener? = null

    private val db = FirebaseDatabase.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private fun messagesRef(roomId: String) = db.getReference("rooms").child(roomId).child("messages")
    private fun typingRef(roomId: String) = db.getReference("rooms").child(roomId).child("typing")

    fun switchRoom(roomId: String, currentUserId: String) {
        messageListener?.let { messagesRef(currentRoom).removeEventListener(it) }
        typingListener?.let { typingRef(currentRoom).removeEventListener(it) }

        currentRoom = roomId

        // Listener para mensagens
        messageListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val msgs = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                _messages.value = msgs.sortedBy { it.timeStamp }
            }
            override fun onCancelled(error: DatabaseError) { /* Tratar erro */ }
        }
        messagesRef(currentRoom).addValueEventListener(messageListener!!)

        // Listener para "digitando"
        typingListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = snapshot.children.mapNotNull {
                    // Ignora o próprio usuário
                    if (it.key != currentUserId) it.getValue(String::class.java) else null
                }
                _typingUsers.value = users
            }
            override fun onCancelled(error: DatabaseError) { /* Tratar erro */ }
        }
        typingRef(currentRoom).addValueEventListener(typingListener!!)
    }

    fun updateTypingStatus(userId: String, userName: String, isTyping: Boolean) {
        val userTypingRef = typingRef(currentRoom).child(userId)
        if (isTyping) {
            userTypingRef.setValue(userName)
        } else {
            userTypingRef.removeValue()
        }
    }

    fun sendMessage(senderId: String, senderName: String, text: String, replyTo: Message? = null) {
        val key = messagesRef(currentRoom).push().key ?: return
        val msg = Message(
            id = key, senderId = senderId, senderName = senderName, text = text,
            timeStamp = System.currentTimeMillis(), replyTo = replyTo, type = MessageType.TEXT
        )
        messagesRef(currentRoom).child(key).setValue(msg)
    }

    fun sendImage(senderId: String, senderName: String, imageUri: Uri, replyTo: Message? = null) {
        val messageKey = messagesRef(currentRoom).push().key ?: return
        val storageRef = storage.reference.child("images/$currentRoom/$messageKey.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    val msg = Message(
                        id = messageKey, senderId = senderId, senderName = senderName, text = "Imagem",
                        timeStamp = System.currentTimeMillis(), replyTo = replyTo,
                        type = MessageType.IMAGE, imageUrl = downloadUrl.toString()
                    )
                    messagesRef(currentRoom).child(messageKey).setValue(msg)
                }
            }
            .addOnFailureListener {
                // Tratar falha no upload
            }
    }

    fun deleteMessage(messageId: String) {
        messagesRef(currentRoom).child(messageId).removeValue()
    }
}