package com.example.cruditemapp.data

data class Item(
    val id: String? = null, // ID gerado pelo Firestore
    val title: String = "",
    val description: String = ""
)