package com.example.tradesphere

data class ChatItem(
    val userId: String,
    val username: String,
    val lastMessage: String = ""
)