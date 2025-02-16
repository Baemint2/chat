package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.message.dto.ChatMessageRequest
import com.moz1mozi.chat.message.dto.ChatMessageResponse
import com.moz1mozi.chat.message.dto.UnreadMessageResponse
import com.moz1mozi.chat.message.service.ChatMessageService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.SliceImpl
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDateTime
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

 @Test
 fun 메시지조회() {

  val testMessages = listOf(
   ChatMessageResponse(803L, 17L, 38L, "?", LocalDateTime.parse("2025-02-13T22:07:41.843584"), Status.ENABLED),
   ChatMessageResponse(802L, 17L, 38L, "하이", LocalDateTime.parse("2025-02-13T22:07:41.041260"), Status.ENABLED),
   ChatMessageResponse(801L, 17L, 38L, "하이", LocalDateTime.parse("2025-02-13T22:07:39.760071"), Status.ENABLED),
   ChatMessageResponse(798L, 17L, 38L, "아", LocalDateTime.parse("2025-02-13T22:07:29.449454"), Status.ENABLED),
  )

  val pageable = PageRequest.of(0, 20)
  val slice = SliceImpl(testMessages, pageable, false)
  `when`(chatMessageService.getMessage(anyLong(), any())).thenReturn(slice)
  val message = chatMessageService.getMessage(38L, pageable)

  assertThat(message.content).hasSize(testMessages.size)
 }

}