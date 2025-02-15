package com.moz1mozi.chat.message.repository

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.message.dto.UnreadMessageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

interface ChatMessageCustomRepository {

    // 안 읽은 메시지 목록
    fun selectUnreadMessages(userId: Long): List<UnreadMessageResponse>

    // 특정 방의 안 읽은 메시지
    fun selectUnreadMessage(chatRoomId: Long, userId: Long): UnreadMessageResponse?

    fun selectMessage(chatRoomId: Long, pageable: Pageable): Slice<ChatMessage>
}