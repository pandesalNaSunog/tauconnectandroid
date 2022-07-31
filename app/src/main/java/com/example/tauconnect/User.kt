package com.example.tauconnect

data class User(
    val created_at: String,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val name: String,
    val profile_picture: String,
    val updated_at: String,
    val user_type: String
)