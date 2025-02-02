package com.moz1mozi.chat.user.repository

import com.moz1mozi.chat.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}