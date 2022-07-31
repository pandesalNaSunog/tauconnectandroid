package com.example.tauconnect

data class PostsItem(
    val comments: List<CommentX>,
    val date: String,
    val description: String,
    val name: String,
    val post_id: Int,
    val profile_picture: String
)