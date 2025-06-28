package com.example.miaomiao.data.repository

import com.example.miaomiao.data.User
import com.example.miaomiao.data.dao.UserDao
import java.security.MessageDigest

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    
    override suspend fun loginUser(phone: String, password: String): Result<User> {
        return try {
            // 首先检查用户是否存在
            val existingUser = userDao.getUserByPhone(phone)
            if (existingUser == null) {
                Result.failure(Exception("账号不存在"))
            } else {
                // 验证密码
                val hashedPassword = hashPassword(password)
                val user = userDao.getUserByPhoneAndPassword(phone, hashedPassword)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("密码错误"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun registerUser(phone: String, password: String, username: String): Result<User> {
        return try {
            // 检查手机号是否已存在
            if (isPhoneExists(phone)) {
                Result.failure(Exception("手机号已被注册"))
            } else {
                // 加密密码
                val hashedPassword = hashPassword(password)
                val user = User(
                    username = username,
                    phone = phone,
                    password = hashedPassword
                )
                userDao.insertUser(user)
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(Exception("注册失败"))
        }
    }
    
    override suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }
    
    override suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    override suspend fun isPhoneExists(phone: String): Boolean {
        return userDao.isPhoneExists(phone) > 0
    }
    
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.fold("") { str, it -> str + "%02x".format(it) }
    }
}