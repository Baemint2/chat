package com.moz1mozi.chat.message.dto

class MsgDeleteRequest(
    val chatRoomId: Long,
    val userId: Long,
    val msgId: Long
) {
}