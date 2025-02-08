package com.moz1mozi.chat.user.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.moz1mozi.chat.entity.User

class UserResponse(
    val id: Long?,
    val username: String,
    @JsonIgnore val password: String,
    val nickname: String? = null,
) {
    companion object {
        fun of(user: User): UserResponse {
          return UserResponse(user.id, user.username, user.password, user.nickname)
        }
    }

    fun toEntity(): User {
        return User(
            id = this.id,
            username = this.username,
            password = this.password,
            nickname = this.nickname
        )
    }

    override fun toString(): String {
        return "UserResponse(username='$username', password='$password', nickname='$nickname')"
    }
}