package com.moz1mozi.chat.message.dto

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.entity.Status
import java.time.LocalDateTime

class ChatMessageResponse(
    val msgId: Long?,
    val userId: Long?,
    val chatRoomNo: Long?,
    val msgContent: String?,
    val msgDt: LocalDateTime,
    var msgStat: Status
) {
    companion object {
        fun from(chatMessage: ChatMessage): ChatMessageResponse {
            return ChatMessageResponse(
                chatMessage.id,
                chatMessage.user.id,
                chatMessage.chatRoom.id,
                chatMessage.msgContent,
                chatMessage.msgDt,
                chatMessage.msgStat!!,
            )
        }
    }

    override fun toString(): String {
        return "ChatMessageResponse(msgId=$msgId, userId=$userId, chatRoomNo=$chatRoomNo, msgContent=$msgContent, msgDt=$msgDt)"
    }


}