package com.moz1mozi.chat.room.dto

import com.moz1mozi.chat.user.dto.UserInfo
import java.time.LocalDateTime

class ChatRoomSearchResponse(
    val chatRoomId: Long,
    val chatRoomTitle: String?,
    val creator: String?,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val participantUsers: List<UserInfo>,
    var latelyMessage: String? = null,
    var unreadCount: Long? = 0,
) {

    override fun toString(): String {
        return "ChatRoomSearchResponse(chatRoomId=$chatRoomId, chatRoomTitle=$chatRoomTitle, creator=$creator, createdAt=$createdAt, updatedAt=$updatedAt, participantUsers=$participantUsers, latelyMessage=$latelyMessage)"
    }
}