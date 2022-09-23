package com.example.tauconnect

data class ComplaintsItem(
    val complaint: String,
    val created_at: String,
    val id: Int,
    val status: String,
    val updated_at: String,
    val user: UserX,
    val user_id: String,
    val category: String
)