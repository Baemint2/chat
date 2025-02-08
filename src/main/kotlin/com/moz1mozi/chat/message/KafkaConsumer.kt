package com.moz1mozi.chat.message

import com.moz1mozi.chat.message.dto.ChatMessageRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service


@Service
class KafkaConsumer(
    private val chatMessageService: ChatMessageService
) {
    private val messagingTemplate: SimpMessageSendingOperations? = null

    private val logger = KotlinLogging.logger {}

    /**
     * Kafka에서 메시지가 발행(publish)되면 대기하고 있던 Kafka Consumer가 해당 메시지를 받아 처리한다.
     */
    @KafkaListener(topics = ["\${spring.kafka.template.default-topic}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun sendMessage(chatMessage: ChatMessageRequest) {
        try {
            logger.info{"Sending message ${chatMessage.toString()}"}

            chatMessageService.saveMessage(chatMessage)
//            messagingTemplate?.convertAndSend(
//                "/sub/chat/room/" + chatMessage.chatRoom.id,
//                chatMessage
//            ) // Websocket 구독자에게 채팅 메시지 Send
        } catch (e: Exception) {
            logger.error(e.message)
        }
    }
}