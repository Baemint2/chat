package com.moz1mozi.chat.user

import com.moz1mozi.chat.config.KafkaConfig
import com.moz1mozi.chat.entity.QChatRoomMng
import com.moz1mozi.chat.entity.QUser
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.user.repository.UserRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    brokerProperties = ["listeners=PLAINTEXT://localhost:9092"],
    ports = [9092]
)
class UserRepositoryTest (
    @Autowired val userRepository: UserRepository,
    @Autowired val entityManager: EntityManager,
) {

    @AfterEach
    fun tearDown() {
        userRepository.deleteAllInBatch()
    }

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    private val logger = KotlinLogging.logger {}

    private val passwordEncoder: PasswordEncoder = BCryptPasswordEncoder()

    @DisplayName("유저가 회원가입을 한다.")
    @Test
    fun saveUser() {
        //given
        val encodedPassword = passwordEncoder.encode("1234")
        val user = User(
            username = "testUsername",
            password = encodedPassword,
            nickname = "testNickname2",
        )

        // when
        val saveUser = userRepository.save(user)

        // then
        assertThat(saveUser)
            .isNotNull
            .extracting("username", "password", "nickname")
            .containsExactly("testUsername", encodedPassword,"testNickname2")

    }

    @DisplayName("가입되어있는 유저의 목록을 조회한다.")
    @Test
    fun findUserList() {
        //given
        val encodedPassword = passwordEncoder.encode("1234")
        val user1 = User(
            username = "testUsername",
            password = encodedPassword,
            nickname = "testNickname",
        )
        val user2 = User(
            username = "testUsername2",
            password = encodedPassword,
            nickname = "testNickname2",
        )

        userRepository.saveAll(listOf(user1, user2))

        // when
        val findAll = userRepository.findAll()

        // then
        assertThat(findAll).hasSize(2)
            .extracting("username", "nickname")
            .containsExactlyInAnyOrder(tuple("testUsername", "testNickname"), tuple("testUsername2", "testNickname2"))

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
        val userList = queryFactory.select(QUser.user)
            .from(QUser.user)
            .leftJoin(QChatRoomMng.chatRoomMng)
            .on(QUser.user.id.eq(QChatRoomMng.chatRoomMng.chatUserPk.user.id))
            .on(QChatRoomMng.chatRoomMng.chatUserPk.chatRoom.id.eq(35L))
            .where(QChatRoomMng.chatRoomMng.chatUserPk.chatRoom.id.isNull)
            .fetch()

        userList.forEach { user -> logger.info { user.username } }
    }

}