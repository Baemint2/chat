package com.moz1mozi.chat.message.repository

import com.moz1mozi.chat.entity.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomRepository: JpaRepository<ChatRoom, Long> {

}