package com.example.tradesphere

data class Message(
    val senderId: String,
    val receiverId: String,
    val text: String,
    val timestamp: Long
)