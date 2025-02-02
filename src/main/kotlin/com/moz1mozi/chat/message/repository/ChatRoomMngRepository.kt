package com.moz1mozi.chat.message.repository

import com.moz1mozi.chat.entity.ChatRoomMng
import org.springframework.data.jpa.repository.JpaRepository

interface ChatRoomMngRepository: JpaRepository<ChatRoomMng, Long> {
}