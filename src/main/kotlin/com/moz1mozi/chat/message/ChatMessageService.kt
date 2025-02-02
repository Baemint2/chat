package com.moz1mozi.chat.message

import com.moz1mozi.chat.message.repository.ChatMessageRepository
import com.moz1mozi.chat.user.UserService
import org.springframework.stereotype.Service

@Service
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomService: ChatRoomService,
    private val userService: UserService,
) {

}