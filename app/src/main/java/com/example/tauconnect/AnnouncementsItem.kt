package com.example.tauconnect

data class AnnouncementsItem(
    val created_at: String,
    val description: String,
    val id: Int,
    val title: String,
    val updated_at: String,
    val user: UserX,
    val user_id: Int,
)