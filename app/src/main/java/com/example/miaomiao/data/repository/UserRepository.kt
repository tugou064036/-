package com.example.miaomiao.data.repository

import com.example.miaomiao.data.User

interface UserRepository {
    suspend fun registerUser(phone: String, password: String, username: String = "喵喵用户"): Result<User>
    suspend fun loginUser(phone: String, password: String): Result<User>
    suspend fun getUserById(userId: String): User?
    suspend fun updateUser(user: User)
    suspend fun isPhoneExists(phone: String): Boolean
}