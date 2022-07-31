package com.example.tauconnect

data class PostsWithTrendingTopics(
    val posts: List<PostsItem>,
    val trending_topics: List<TrendingTopicItem>
)