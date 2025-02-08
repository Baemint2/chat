package com.moz1mozi.chat.user.repository

import com.moz1mozi.chat.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long>, UserCustomRepository {
    fun findByUsername(username: String): User?
    fun findByNickname(nickname: String): User?
    fun findAllByUsernameIsNot(username: String): List<User>
}