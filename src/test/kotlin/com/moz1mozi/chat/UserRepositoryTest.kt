package com.moz1mozi.chat

import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest constructor(
    @Autowired val userRepository: UserRepository,
) {
    @MockitoBean private lateinit var passwordEncoder: PasswordEncoder

    private val logger = KotlinLogging.logger {}

    @Test
    fun saveUser() {
        `when`(passwordEncoder.encode(anyString())).thenReturn("encodedPassword")
        val saveUser = userRepository.save(
            User(
                username = UUID.randomUUID().toString(),
                password = passwordEncoder.encode("1234"),
                nickname = "testNickname2",
            )
        )
        logger.info { "testUser created, password: ${saveUser.password}" }
    }

    @Test
    fun findUserList() {
        val findAll = userRepository.findAll()
        logger.info { "findAll users: ${findAll.size}" }
    }

    @Test
    fun searchUserList() {
        val searchUsers = userRepository.searchUsers("m")
        for (searchUser in searchUsers) {
            logger.info { searchUser.username }
        }
    }

}