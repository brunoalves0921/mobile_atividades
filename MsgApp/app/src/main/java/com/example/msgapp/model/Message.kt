package com.example.msgapp.model

// Definindo os tipos de mensagem para clareza
enum class MessageType {
    TEXT, IMAGE, AUDIO
}

data class Message(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "", // Usado para texto ou legenda da foto/áudio
    val timeStamp: Long = 0L,
    val replyTo: Message? = null,

    // Novos campos
    val type: MessageType = MessageType.TEXT,
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val audioDuration: Long? = null
)