package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.*
import com.moz1mozi.chat.message.repository.ChatRoomMngRepository
import com.moz1mozi.chat.message.repository.ChatRoomRepository
import com.moz1mozi.chat.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Rollback

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatRoomRepositoryTest(
    @Autowired val chatRoomRepository: ChatRoomRepository,
    @Autowired val chatRoomMngRepository: ChatRoomMngRepository
) {
    @Autowired
    private lateinit var userRepository: UserRepository
    private val logger = KotlinLogging.logger {}

    @Test
    @DisplayName("채팅방을 생성한다.")
    fun 채팅방_생성() {
        val chatRoom = ChatRoom(chatRoomTitle = "테스트 채팅방").apply {
            creator = "testUsername"
        }
        val save = chatRoomRepository.save(chatRoom)
        logger.info { "채팅방 생성: ${chatRoom.id}, ${chatRoom.chatRoomTitle}, ${chatRoom.chatRoomStat}, ${chatRoom.creator}, ${chatRoom.chatRoomMng}" }
    }

    @Test
    @DisplayName("채팅방을 조회한다.")
    fun 채팅방_조회() {
        val findChatRoom = chatRoomRepository.findById(anyLong())
        logger.info{"findChatRoom: ${findChatRoom.get().chatRoomTitle}"}
    }
}