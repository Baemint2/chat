package com.moz1mozi.chat.message.dto

class UnreadMessageResponse(
    val chatRoomId: Long,
    val userId: Long,
    val unreadCount: Long?,
) {

    override fun toString(): String {
        return "UnreadMessageResponse(chatRoomId=$chatRoomId, userId=$userId, unreadCount=$unreadCount)"
    }
}