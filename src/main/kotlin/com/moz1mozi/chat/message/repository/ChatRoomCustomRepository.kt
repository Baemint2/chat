package com.moz1mozi.chat.message.repository

import com.moz1mozi.chat.message.dto.ChatRoomResponse

interface ChatRoomCustomRepository {

    fun selectChatRoom(username: String): List<ChatRoomResponse>
}