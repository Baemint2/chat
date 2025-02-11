package com.moz1mozi.chat.message.controller

import com.moz1mozi.chat.message.service.ChatMessageService
import com.moz1mozi.chat.message.dto.ChatMessageRequest
import com.moz1mozi.chat.message.dto.ChatMessageResponse
import com.moz1mozi.chat.user.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/message")
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
    private val userService: UserService,
    private val messagingTemplate: SimpMessagingTemplate,
    private val kafkaTemplate: KafkaTemplate<String, ChatMessageRequest>? = null
) {

    val logger = KotlinLogging.logger {}

    @PostMapping("/get")
    fun getMessages(@RequestBody chatMessageRequest: ChatMessageRequest): ResponseEntity<Map<String, List<ChatMessageResponse>>> {

        val message = chatMessageService.getMessage(chatMessageRequest.chatRoomNo, chatMessageRequest.userId)
        return ResponseEntity.ok().body(mapOf("message" to message))
    }

    @MessageMapping("/chat/message")
    fun message(message: ChatMessageRequest) {
        kafkaTemplate!!.send("chat-messages", message)
    }

    @MessageMapping("/unread")
    @SendToUser("/queue/unreadCount")
    fun unreadMessages(principal: Principal) {

        val findUser = userService.findUser(principal.name)
        val unreadMessages = chatMessageService.getUnreadMessages(findUser?.id!!)
        logger.info { "안읽은 메시지 ${unreadMessages.toString()}" }
        messagingTemplate.convertAndSendToUser(principal.name, "/queue/unreadCount", unreadMessages)

    }
}