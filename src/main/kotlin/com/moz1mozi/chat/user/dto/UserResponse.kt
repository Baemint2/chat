package com.moz1mozi.chat.user.dto

import com.moz1mozi.chat.entity.User

class UserResponse(
    val username: String,
    val password: String,
    private val nickname: String? = null,
) {
    companion object {
        fun of(user: User): UserResponse {
          return UserResponse(user.username, user.password, user.nickname)
        }
    }

    fun toEntity(): User {
        return User(
            username = this.username,
            password = this.password,
            nickname = this.nickname
        )
    }

    override fun toString(): String {
        return "UserResponse(username='$username', password='$password', nickname='$nickname')"
    }
}