package com.moz1mozi.chat.user

import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.user.dto.UserResponse
import com.moz1mozi.chat.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {

    private val logger = KotlinLogging.logger { }

    @Transactional
    fun saveUser(user: User): UserResponse {
        if(userRepository.findByUsername(user.username) != null) {
            throw IllegalArgumentException("User already exists")
        }
        val savedUser = User(
            username = user.username,
            password = passwordEncoder.encode(user.password),
            nickname = user.nickname,
        )

        return UserResponse.of(savedUser)
    }

    @Transactional
    fun findUser(username: String): UserResponse? {
        val user = userRepository.findByUsername(username)
        requireNotNull(user) { "Username not found" }
        return UserResponse.of(user)
    }

}