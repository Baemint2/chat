package com.moz1mozi.chat.message

import com.moz1mozi.chat.message.dto.ChatMessageRequest
import com.moz1mozi.chat.user.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test

@SpringBootTest
class ChatMessageServiceTest @Autowired constructor(
 val chatMessageService: ChatMessageService,
 val chatRoomService: ChatRoomService,
 val userService: UserService,
) {

 val logger = KotlinLogging.logger {}
  @Test
  @DisplayName("메시지를 저장합니다.")
  fun 메시지저장테스트() {

  val chatMessageRequest = ChatMessageRequest(
   userId = 17L,
   creator = "moz1mozi",
   msgContent = "123",
   chatRoomNo = 48L
  )
   val saveMessage = chatMessageService.saveMessage(chatMessageRequest)

   saveMessage.join();


  }

}