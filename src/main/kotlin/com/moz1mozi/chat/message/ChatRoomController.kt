package com.moz1mozi.chat.message

import com.moz1mozi.chat.message.dto.ChatRoomRequest
import com.moz1mozi.chat.message.dto.ChatRoomResponse
import com.moz1mozi.chat.message.dto.ChatRoomSearchResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Controller
class ChatRoomController(
    private val chatRoomService: ChatRoomService
) {

    private val logger = KotlinLogging.logger { }

    @PostMapping("/chatRoom")
    fun createChatRoom(@RequestBody chatRoom: ChatRoomRequest): ResponseEntity<Map<String, Any>> {
        val getUsername = SecurityContextHolder.getContext().authentication.name
        val createChatRoom = chatRoomService.createChatRoom(chatRoom, getUsername)
        return ResponseEntity.ok().body(mapOf("message" to "채팅방이 생성되었습니다.", "chatRoom" to createChatRoom))
    }

    @GetMapping("/chatRoom/{username}")
    fun getChatRoom(@PathVariable username: String): ResponseEntity<Map<String, List<ChatRoomSearchResponse>>> {
        val chatRoom = chatRoomService.getChatRoom(username)
        return ResponseEntity.ok().body(mapOf("chatRoom" to chatRoom))
    }
}