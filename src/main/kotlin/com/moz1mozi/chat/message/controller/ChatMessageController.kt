package com.moz1mozi.chat.message.controller

import com.moz1mozi.chat.message.dto.ChatMessageResponse
import com.moz1mozi.chat.message.service.ChatMessageService
import com.moz1mozi.chat.user.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.web.PageableDefault
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequestMapping("/message")
class ChatMessageController(
    private val chatMessageService: ChatMessageService,
    private val userService: UserService,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    val logger = KotlinLogging.logger {}

    @GetMapping("/get/{chatRoomId}")
    fun getMessages(@PathVariable chatRoomId: Long,
                    @PageableDefault(size = 20) pageable: Pageable
    ): Slice<ChatMessageResponse> {

        return chatMessageService.getMessage(chatRoomId, pageable)
    }

    @MessageMapping("/unread")
    fun unreadMessages(principal: Principal) {

        val findUser = userService.findUser(principal.name)
        val unreadMessages = chatMessageService.getUnreadMessages(findUser?.id!!)
        logger.info { "안읽은 메시지 ${unreadMessages.toString()}" }
        messagingTemplate.convertAndSend("/queue/unreadCount/${principal.name}", unreadMessages)

    }
}