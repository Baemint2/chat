package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.QChatRoom.chatRoom
import com.moz1mozi.chat.entity.QChatRoomMng.chatRoomMng
import com.moz1mozi.chat.entity.QUser.user
import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.room.dto.ChatRoomSearchResponse
import com.moz1mozi.chat.room.repository.ChatRoomMngRepository
import com.moz1mozi.chat.room.repository.ChatRoomRepository
import com.moz1mozi.chat.user.dto.UserInfo
import com.moz1mozi.chat.user.repository.UserRepository
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatRoomRepositoryTest(
    @Autowired val chatRoomRepository: ChatRoomRepository,
    @Autowired val chatRoomMngRepository: ChatRoomMngRepository,
    @Autowired private val em: EntityManager,
) {

    private var jpaQueryFactory: JPAQueryFactory = JPAQueryFactory(em)
    @Autowired
    private lateinit var userRepository: UserRepository

    private val logger = KotlinLogging.logger {}

    @Test
    @DisplayName("채팅방을 생성한다.")
    fun 채팅방_생성() {
        val chatRoom = ChatRoom(chatRoomTitle = "테스트 채팅방").apply {
            creator = "testUsername"
        }

        logger.info { "채팅방 생성: ${chatRoom.id}, ${chatRoom.chatRoomTitle}, ${chatRoom.chatRoomStat}, ${chatRoom.creator}, ${chatRoom.chatRoomMng}" }
    }

    @Test
    @DisplayName("채팅방을 조회한다.")
    fun 채팅방_조회() {
        val groupConcat = Expressions.stringTemplate(
            "GROUP_CONCAT({0})", user.username
        )

        val subQuery = JPAExpressions
            .select(chatRoomMng.chatUserPk.chatRoom.id)
            .from(chatRoomMng)
            .join(user).on(chatRoomMng.chatUserPk.user.id.eq(user.id))
            .where(user.username.eq("testUser"), chatRoomMng.entryStat.eq(Status.ENABLED))

        val results = jpaQueryFactory
            .select(chatRoom.id,
                chatRoom.chatRoomTitle,
                chatRoom.creator,
                chatRoom.createdAt,
                chatRoom.updatedAt,
                groupConcat)
            .from(chatRoom)
            .join(chatRoomMng)
            .on(chatRoom.id.eq(chatRoomMng.chatUserPk.chatRoom.id))
            .join(user)
            .on(chatRoomMng.chatUserPk.user.id.eq(user.id))
            .where(chatRoomMng.entryStat.eq(Status.ENABLED)
                , chatRoom.chatRoomStat.eq(Status.ENABLED)
                , chatRoom.id.`in`(subQuery))
            .groupBy(chatRoom.id)
            .fetch()
        results.map { tuple ->
            val usernames = tuple.get(groupConcat)?.split(",") ?: emptyList()

            val userInfos = usernames.map { username ->
                val userEntity = jpaQueryFactory
                    .select(user)
                    .from(user)
                    .where(user.username.eq(username))
                    .fetchOne()

                UserInfo(
                    username = userEntity?.username ?: "Unknown",
                    nickname = userEntity?.nickname ?: "Unknown",
                )
            }

            ChatRoomSearchResponse(
                chatRoomId = tuple.get(chatRoom.id)!!,
                chatRoomTitle = tuple.get(chatRoom.chatRoomTitle)!!,
                creator = tuple.get(chatRoom.creator)!!,
                createdAt = tuple.get(chatRoom.createdAt)!!,
                updatedAt = tuple.get(chatRoom.updatedAt)!!,
                participantUsers = userInfos
            )
        }
        logger.info { "$results" }
    }
}