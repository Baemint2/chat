package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.ChatUserPK
import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.room.repository.ChatRoomMngRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertNotEquals

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatRoomMngRepositoryTest(
    @Autowired private val chatRoomMngRepository: ChatRoomMngRepository,
) {

    val logger = KotlinLogging.logger {}
    @Test
    @Transactional
    fun 채팅방나가기() {

        val chatUserPK = ChatUserPK(
            ChatRoom(id = 10L, chatRoomTitle = null, chatRoomStat = Status.ENABLED),
            User(id = 2L, username = "testUser", password = "1234")
        )
        val findById = chatRoomMngRepository.findById(chatUserPK).orElseThrow()

        chatRoomMngRepository.updateEntryStat(10L, 2L, Status.DISABLED)

        val findById2 = chatRoomMngRepository.findById(chatUserPK).orElseThrow()

        assertNotEquals(findById, findById2)
    }
}