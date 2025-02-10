package com.moz1mozi.chat.message

import com.moz1mozi.chat.message.dto.AccessDtUpdateRequest
import com.moz1mozi.chat.message.dto.ChatMessageRequest
import com.moz1mozi.chat.message.dto.ChatRoomRequest
import com.moz1mozi.chat.message.dto.ChatRoomSearchResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class ChatRoomController(
    private val chatRoomService: ChatRoomService,
    private val chatMessageService: ChatMessageService,
    private val kafkaTemplate: KafkaTemplate<String, ChatMessageRequest>? = null

) {

    private val logger = KotlinLogging.logger { }

    @PostMapping("/chatRoom")
    fun createChatRoom(@RequestBody chatRoom: ChatRoomRequest): ResponseEntity<Map<String, Any?>> {
        val createChatRoom = chatRoom.creator?.let {
            chatRoomService.createChatRoom(chatRoom,
                it, chatRoom.usernameList)
        }
        return ResponseEntity.ok().body(mapOf("message" to "채팅방이 생성되었습니다.", "chatRoom" to createChatRoom))
    }

    // 현재 로그인한 유저가 속해있는 채팅방 조회
    @GetMapping("/chatRoom/{username}")
    fun getChatRoom(@PathVariable username: String): ResponseEntity<Map<String, List<ChatRoomSearchResponse>>> {
        val chatRoom = chatRoomService.findChatRoomByUsername(username)
        return ResponseEntity.ok().body(mapOf("chatRoom" to chatRoom))
    }

    @MessageMapping("/chat/access-update")
    fun updateAccess(@Payload accessDtUpdateRequest: AccessDtUpdateRequest) {
        logger.info{"Access update request: $accessDtUpdateRequest"}
        chatRoomService.updateEntryDt(accessDtUpdateRequest.chatRoomId, accessDtUpdateRequest.userId)
    }
}