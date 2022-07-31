package com.example.tauconnect

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface Requests {
    @POST("/tauApi/public/api/login")
    suspend fun login(@Body request: RequestBody): LoginDetails

    @GET("/tauApi/public/api/posts")
    suspend fun getPosts(): PostsWithTrendingTopics

    @POST("/tauApi/public/api/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<ResponseBody>

    @POST("/tauApi/public/api/post-comment")
    suspend fun postComment(@Header("Authorization") token: String, @Body request: RequestBody): CommentX

    @GET("/tauApi/public/api/my-profile")
    suspend fun getProfile(@Header("Authorization") token: String): User

    @GET("/tauApi/public/api/my-complaints")
    suspend fun getComplaints(@Header("Authorization") token: String): Complaints

    @POST("/tauApi/public/api/submit-complaint")
    suspend fun submitComplaint(@Header("Authorization") token: String, @Body request: RequestBody): ComplaintsItem

    @GET("tauApi/public/api/announcements")
    suspend fun getAnnouncements(): Announcements

    @GET("tauApi/public/api/users-to-message")
    suspend fun getUsers(@Header("Authorization") token: String): Users

    @POST("tauApi/public/api/conversation")
    suspend fun getConversation(@Header("Authorization") token: String, @Body request: RequestBody): MessagesX

    @POST("tauApi/public/api/send-message")
    suspend fun sendMessage(@Header("Authorization") token: String, @Body request: RequestBody): MessagesItem

    @POST("tauApi/public/api/update-profile-picture")
    suspend fun updateProfilePicture(@Header("Authorization") token: String, @Body request: RequestBody): ProfilePicture

    @POST("tauApi/public/api/update-profile")
    suspend fun updateProfile(@Header("Authorization") token: String, @Body request: RequestBody): User

    @POST("tauApi/public/api/write-post")
    suspend fun writePost(@Header("Authorization") token: String, @Body request: RequestBody): PostsItem

    @POST("tauApi/public/api/post-comments")
    suspend fun getPostComments(@Header("Authorization") token: String, @Body request: RequestBody): List<CommentX>

    @POST("tauApi/public/api/post-announcement")
    suspend fun postAnnouncement(@Header("Authorization") token: String, @Body request: RequestBody): AnnouncementsItem
}