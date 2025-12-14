package com.example.chuckerdemo.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("posts")
    suspend fun getPosts(): Response<List<PostDto>>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Response<PostDto>

    @POST("posts")
    suspend fun createPost(@Body post: PostDto): Response<PostDto>

    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: PostDto): Response<PostDto>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<Unit>

    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Int): Response<UserDto>

    @GET("error")
    suspend fun getError(): Response<Unit>

    @GET("slow")
    suspend fun getSlowResponse(): Response<Map<String, String>>

    @GET("health")
    suspend fun getHealth(): Response<Map<String, Any>>

    @POST("calculateFactorial")
    suspend fun calculateFactorial(@Body body: Map<String, Int>):
            Response<FactorialResponse>
}