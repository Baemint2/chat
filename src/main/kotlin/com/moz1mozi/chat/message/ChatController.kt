package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.ChatMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller


@Controller
class ChatController {
    private val logger = KotlinLogging.logger {}
    private val kafkaTemplate: KafkaTemplate<String, ChatMessage>? = null

    @MessageMapping("/chat/message") // ✅ 웹에서 "/pub/chat/message"로 메시지 전송
    fun message(message: ChatMessage) {
        println("Received Message: " + message.msgContent)
        kafkaTemplate!!.send("chat-messages", message)
    }
}
