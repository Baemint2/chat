package com.moz1mozi.chat.room.repository

import com.moz1mozi.chat.room.dto.ChatRoomSearchResponse

interface ChatRoomCustomRepository {

    fun selectChatRoom(username: String): List<ChatRoomSearchResponse>

}