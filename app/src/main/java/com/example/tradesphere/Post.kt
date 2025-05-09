package com.example.tradesphere.fragments

data class Post(
    val postId: String,
    val title: String,
    val text: String,
    val category: String,
    val imageUrl: String?
)