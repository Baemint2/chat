package com.moz1mozi.chat.message.dto

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.user.dto.UserInfo
import java.time.LocalDateTime

class ChatMessageRequest(
   val chatRoomId: Long,
   val chatRoomTitle: String?,
   val createdAt: LocalDateTime? = null,
   val updatedAt: LocalDateTime? = null,
   val participantUsers: List<UserInfo>?,
   var latelyMessage: String? = null,
   val userId: Long,
   val username: String? = null,
   val creator: String?,
   val msgContent: String?,
   val unreadCount: Long?,
) {

    fun toEntity(user: User, chatRoom: ChatRoom): ChatMessage {
        return ChatMessage(
            user = user,
            chatRoom = chatRoom,
            msgContent = msgContent,
        )
    }
    override fun toString(): String {
        return "ChatMessageRequest(chatRoomId=$chatRoomId, chatRoomTitle=$chatRoomTitle, createdAt=$createdAt, updatedAt=$updatedAt, participantUsers=$participantUsers, latelyMessage=$latelyMessage, userId=$userId, creator=$creator, msgContent=$msgContent, unreadCount=$unreadCount)"
    }


}