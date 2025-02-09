package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.message.repository.ChatMessageRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatMessageRepositoryTest(
    @Autowired val chatMessageRepository: ChatMessageRepository,
) {

    private val logger = KotlinLogging.logger {}

    @Test
    fun 채팅메시지전송() {
        val testUser = User(id = 3L,"testUser", "1234", "테스트유저")

        val testChatRoom = ChatRoom("테스트채팅방").apply {
            id = 7L
        }

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
}