package com.moz1mozi.chat.message

import com.moz1mozi.chat.message.dto.ChatMessageResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/message")
class ChatMessageController(
    private val chatMessageService: ChatMessageService
) {

    val logger = KotlinLogging.logger {}

    @PostMapping("/get")
    fun getMessages(@RequestBody chatRoomNo: Long): ResponseEntity<Map<String, List<ChatMessageResponse>>> {
        val message = chatMessageService.getMessage(chatRoomNo)
        return ResponseEntity.ok().body(mapOf("message" to message))
    }
}