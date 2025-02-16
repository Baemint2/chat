package com.moz1mozi.chat.room.controller

import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.room.dto.ChatRoomRequest
import com.moz1mozi.chat.room.dto.ChatRoomSearchResponse
import com.moz1mozi.chat.room.dto.DtUpdateRequest
import com.moz1mozi.chat.room.service.ChatRoomService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat-room")
class ChatRoomController(
    private val chatRoomService: ChatRoomService,
    ) {

    private val logger = KotlinLogging.logger { }

    @PostMapping
    fun createChatRoom(@RequestBody chatRoom: ChatRoomRequest): ResponseEntity<Map<String, Any?>> {
        val createChatRoom = chatRoom.creator?.let {
            chatRoomService.createChatRoom(chatRoom,
                it, chatRoom.usernameList)
        }
        return ResponseEntity.ok().body(mapOf("message" to "채팅방이 생성되었습니다.", "chatRoom" to createChatRoom))
    }

    // 현재 로그인한 유저가 속해있는 채팅방 조회
    @GetMapping("/{username}")
    fun getChatRoom(@PathVariable username: String): ResponseEntity<Map<String, List<ChatRoomSearchResponse>>> {
        val chatRoom = chatRoomService.findChatRoomByUsername(username)
        return ResponseEntity.ok().body(mapOf("chatRoom" to chatRoom))
    }

    @MessageMapping("/chat/access-update")
    fun updateAccess(@Payload dtUpdateRequest: DtUpdateRequest) {
        logger.info{"Access update request: $dtUpdateRequest"}
        chatRoomService.updateEntryDt(dtUpdateRequest.chatRoomId, dtUpdateRequest.username)
    }

    @PostMapping("/last-seen-update")
    fun updateLastSeen(@RequestBody dtUpdateRequest: DtUpdateRequest) {
        logger.info{"Last seen update request: $dtUpdateRequest"}
        chatRoomService.updateLastSeenDt(dtUpdateRequest.chatRoomId, dtUpdateRequest.username)
    }

    @PostMapping("/leave")
    fun leaveChatRoom(@RequestBody dtUpdateRequest: DtUpdateRequest): ResponseEntity<Void> {
        chatRoomService.updateEntryStat(dtUpdateRequest.chatRoomId, dtUpdateRequest.username, Status.DISABLED)
        return ResponseEntity.noContent().build()
    }
}