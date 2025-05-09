package com.example.tradesphere

data class User(
    val description: String = "",
    val email: String = "",
    val followers: Long = 0,
    val following: Long = 0,
    val name: String = "",
    val phone: String = "",
    val username: String = ""
)