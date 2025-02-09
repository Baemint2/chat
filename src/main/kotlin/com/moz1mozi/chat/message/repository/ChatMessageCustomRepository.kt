package com.moz1mozi.chat.message.repository

import com.moz1mozi.chat.message.dto.UnreadMessageResponse

interface ChatMessageCustomRepository {

    fun selectUnreadMessages(userId: Long): List<UnreadMessageResponse>
}