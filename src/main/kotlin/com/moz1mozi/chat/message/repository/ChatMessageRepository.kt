package com.moz1mozi.chat.message.repository

import com.moz1mozi.chat.entity.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository

interface ChatMessageRepository: JpaRepository<ChatMessage, Long> {
}