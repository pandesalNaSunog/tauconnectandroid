package com.example.tauconnect

data class NotificationItem(
    val created_at: String,
    val id: Int,
    val message: String,
    val read: String,
    val title: String,
    val updated_at: String,
    val user_id: Int
)