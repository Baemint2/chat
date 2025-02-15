package com.moz1mozi.chat.message

import com.moz1mozi.chat.message.dto.ChatMessageRequest
import com.moz1mozi.chat.message.service.ChatMessageService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class KafkaConsumer(
    private val chatMessageService: ChatMessageService,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * Kafka에서 메시지가 발행(publish)되면 대기하고 있던 Kafka Consumer가 해당 메시지를 받아 처리한다.
     */
    @KafkaListener(topics = ["\${spring.kafka.template.default-topic}"], groupId = "\${spring.kafka.consumer.group-id}")
    fun sendMessage(chatMessage: ChatMessageRequest) {
        try {
            chatMessageService.saveAndNotifyParticipants(chatMessage)
        } catch (e: Exception) {
            logger.error { e.message }
        }
    }
}