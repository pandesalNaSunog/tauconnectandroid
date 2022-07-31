package com.example.tauconnect

data class MessagesItem(
    val created_at: String,
    val id: Int,
    val message: String,
    val mine: Boolean,
    val read: String,
    val receiver_id: String,
    val sender_id: String,
    val updated_at: String
)