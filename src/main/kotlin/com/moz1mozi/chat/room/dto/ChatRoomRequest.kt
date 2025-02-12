package com.moz1mozi.chat.room.dto

import com.moz1mozi.chat.entity.ChatRoom

class ChatRoomRequest(
    private val chatRoomTitle: String? = null,
    var creator: String? = null,
    val usernameList: MutableList<String> = mutableListOf()
) {

    companion object {
        fun of(chatRoom: ChatRoom): ChatRoomRequest {
            return ChatRoomRequest(chatRoom.chatRoomTitle, chatRoom.creator)
        }
    }

    fun toEntity(): ChatRoom {
        return ChatRoom(
            chatRoomTitle = this.chatRoomTitle,
        )
    }

    override fun toString(): String {
        return "ChatRoomRequest(chatRoomTitle='$chatRoomTitle', creator=$creator)"
    }
}