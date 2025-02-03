package com.moz1mozi.chat.message.dto

import java.time.LocalDateTime

class ChatRoomSearchResponse(
    val chatRoomId: Long,
    val chatRoomTitle: String,
    val creator: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val participantUsers: String,
) {
    override fun toString(): String {
        return "ChatRoomResponse(chatRoomId=$chatRoomId, chatRoomTitle='$chatRoomTitle', creator='$creator', createdAt=$createdAt, updatedAt=$updatedAt, participantUsers='$participantUsers')"
    }
}