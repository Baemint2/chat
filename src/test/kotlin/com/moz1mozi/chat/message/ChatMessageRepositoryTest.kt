package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.QChatMessage.chatMessage
import com.moz1mozi.chat.entity.QChatRoomMng.chatRoomMng
import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.message.repository.ChatMessageRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime
import java.util.*

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatMessageRepositoryTest(
    @Autowired val chatMessageRepository: ChatMessageRepository,
    @Autowired private val em: EntityManager
) {

    private val logger = KotlinLogging.logger {}
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(em);

    @Test
    fun 채팅메시지전송() {
        val testUser = User(id = null, UUID.randomUUID().toString(), "1234", "테스트유저")

        val testChatRoom = ChatRoom(id = null, "테스트채팅방")

        em.persist(testUser)
        em.persist(testChatRoom)
        em.flush()

        val chatMessage = ChatMessage("안녕하세요?", testChatRoom, testUser).apply {
            creator = testUser.username;
        }
        chatMessageRepository.save(chatMessage);
        logger.info { "테스트 메시지: ${chatMessage.msgContent}, ${chatMessage.chatRoom.id}, ${chatMessage.user.id}, ${chatMessage.msgDt}, ${chatMessage.msgStat}" }
    }

    @Test
    fun 최신메시지조회() {
        val selectLatelyMessage = chatMessageRepository.selectLatelyMessage(33L)
        logger.info { "selectLatelyMessage: $selectLatelyMessage" }
    }

    @Test
    fun 안읽은메시지조회() {
        val fetch = queryFactory
            .select(
                chatRoomMng.chatUserPk.chatRoom.id,
                chatRoomMng.chatUserPk.user.id,
                chatMessage.id.count().`as`("unreadCount")
            )
            .from(chatRoomMng)
            .join(chatMessage)
            .on(chatRoomMng.chatUserPk.chatRoom.id.eq(chatMessage.chatRoom.id))
            .where(chatRoomMng.chatUserPk.user.id.eq(17L))
            .where(chatRoomMng.entryDt.before(LocalDateTime.now()))
            .where(chatRoomMng.entryStat.eq(Status.ENABLED))
            .groupBy(chatRoomMng.chatUserPk.chatRoom.id)
            .fetch()
//        logger.info { "selectUnreadMessages: $selectUnreadMessages" }

    }

}