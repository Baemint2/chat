package com.moz1mozi.chat.message.repository

import com.moz1mozi.chat.entity.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ChatMessageRepository: JpaRepository<ChatMessage, Long>, ChatMessageCustomRepository {
    fun findAllByChatRoomId(chatRoomId: Long): List<ChatMessage>

    @Query("select c " +
            " from ChatMessage c " +
            "where c.chatRoom.id = :chatRoomId " +
            "order by c.msgDt desc " +
            "limit 1")
    fun selectLatelyMessage(chatRoomId: Long): ChatMessage?
}