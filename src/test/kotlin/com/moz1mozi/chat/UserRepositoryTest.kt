package com.moz1mozi.chat

import com.moz1mozi.chat.entity.QChatRoomMng.chatRoomMng
import com.moz1mozi.chat.entity.QUser.user
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.user.repository.UserRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
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
class UserRepositoryTest (
    @Autowired val userRepository: UserRepository,
    @Autowired private val entityManager: EntityManager,
) {
    @MockitoBean private lateinit var passwordEncoder: PasswordEncoder
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

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

    @Test
    fun selectUsersNotInChatRoom() {
        val userList = queryFactory.select(user)
            .from(user)
            .leftJoin(chatRoomMng)
            .on(user.id.eq(chatRoomMng.chatUserPk.user.id))
            .on(chatRoomMng.chatUserPk.chatRoom.id.eq(35L))
            .where(chatRoomMng.chatUserPk.chatRoom.id.isNull)
            .fetch()

        userList.forEach { user -> logger.info { user.username } }
    }

}