package com.moz1mozi.chat.message.service

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.message.repository.ChatMessageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatMessageQueryService(
    private val chatMessageRepository: ChatMessageRepository
) {

    // 최신 메시지 조회
    @Transactional
    fun findLatelyMessage(chatRoomId: Long): ChatMessage? {
        return chatMessageRepository.selectLatelyMessage(chatRoomId)
    }
}