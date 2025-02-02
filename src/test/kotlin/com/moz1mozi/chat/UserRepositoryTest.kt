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


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest(
    @Autowired val userRepository: UserRepository,
) {
    @MockitoBean private lateinit var passwordEncoder: PasswordEncoder

    private val logger = KotlinLogging.logger {}

    @Test
    fun saveUser() {
        `when`(passwordEncoder.encode(anyString())).thenReturn("encodedPassword")
        val saveUser = userRepository.save(
            User(
                username = "testUser2",
                password = passwordEncoder.encode("1234"),
                nickname = "testNickname2",
            )
        )
        logger.info { "testUser created, password: ${saveUser.password}" }
    }

}