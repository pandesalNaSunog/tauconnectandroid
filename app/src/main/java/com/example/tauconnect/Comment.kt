package com.example.tauconnect

data class Comment(
    val comment: String,
    val created_at: String,
    val id: Int,
    val post_id: String,
    val updated_at: String,
    val user_id: String
)