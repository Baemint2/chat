package com.moz1mozi.chat.message

import com.moz1mozi.chat.message.dto.ChatMessageRequest
import com.moz1mozi.chat.message.dto.UnreadMessageResponse
import com.moz1mozi.chat.message.service.ChatMessageService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import kotlin.test.Test

@SpringBootTest
class ChatMessageServiceTest @Autowired constructor(
 @MockitoBean val chatMessageService: ChatMessageService,
) {

 val logger = KotlinLogging.logger {}
  @Test
  @DisplayName("메시지를 저장합니다.")
  fun 메시지저장테스트() {

  val chatMessageRequest = ChatMessageRequest(
   userId = 17L,
   creator = "moz1mozi",
   msgContent = "123",
   chatRoomId = 48L
  )
   val saveMessage = chatMessageService.saveMessage(chatMessageRequest)

   saveMessage.join();
  }

 @Test
 fun 안읽은메시지조회() {
  val unread1 = UnreadMessageResponse(chatRoomId = 7L, userId = 17L, unreadCount = 0)
  val unread2 = UnreadMessageResponse(chatRoomId = 7L, userId = 17L, unreadCount = 5)
  val unreadMessageList = listOf(unread1, unread2)
  `when`(chatMessageService.getUnreadMessages(anyLong())).thenReturn(unreadMessageList);
  val unreadMessages = chatMessageService.getUnreadMessages(17L)
  unreadMessages.forEach { logger.info { it } }
 }

}