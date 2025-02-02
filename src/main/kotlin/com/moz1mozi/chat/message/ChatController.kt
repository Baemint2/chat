package com.moz1mozi.chat.message

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller


@Controller
class ChatController {
    private val logger = KotlinLogging.logger {}

    @MessageMapping("/message") // 클라이언트가 "/pub/message"로 메시지 전송하면 실행됨
    @SendTo("/sub/chatroom") // "/sub/chatroom"을 구독한 모든 클라이언트에게 메시지 전송
    fun sendMessage(message: ChatMessage): ChatMessage {
        logger.info {"받은 메시지: $message"}
        return message
    }

    open class ChatMessage(
        val message: String
    ) {
        override fun toString(): String {
            return "ChatMessage(message='$message')"
        }
    }
}
