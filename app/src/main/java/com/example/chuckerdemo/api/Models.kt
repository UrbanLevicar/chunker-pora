package com.example.chuckerdemo.api

data class PostDto(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int,
    val createdAt: String? = null
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String
)
data class FactorialResponse(
    val factorial: Long
)