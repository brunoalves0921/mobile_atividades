package com.example.msgapp.model

import com.google.firebase.Timestamp

data class Message (
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val text: String = "",
    val timeStamp: Long = 0L
)

