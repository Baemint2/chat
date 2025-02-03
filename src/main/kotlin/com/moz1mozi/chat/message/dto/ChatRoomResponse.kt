package com.moz1mozi.chat.message.dto

import com.moz1mozi.chat.entity.ChatRoom

class ChatRoomResponse(
    val id: Long? = null,
    val chatRoomTitle: String? = null,
    val creator: String? = null,
) {
    companion object {
        fun from(chatRoom: ChatRoom): ChatRoomResponse {
            return ChatRoomResponse(
                id = chatRoom.id,
                chatRoomTitle = chatRoom.chatRoomTitle,
                creator = chatRoom.creator
            )
        }
    }

    fun toEntity(): ChatRoom {
        return ChatRoom(
            chatRoomTitle = this.chatRoomTitle,
        )
    }
}