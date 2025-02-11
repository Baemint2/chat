package com.moz1mozi.chat.message.dto

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.User

class ChatMessageRequest(
   val userId: Long,
   val creator: String?,
   val msgContent: String?,
   val chatRoomNo: Long,
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
        return "ChatMessageRequest(userId=$userId, creator=$creator, msgContent=$msgContent, chatRoomNo=$chatRoomNo, unreadCount=$unreadCount)"
    }


}