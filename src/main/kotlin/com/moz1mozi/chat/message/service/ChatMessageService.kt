package com.moz1mozi.chat.message.service

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.message.dto.ChatMessageRequest
import com.moz1mozi.chat.message.dto.ChatMessageResponse
import com.moz1mozi.chat.message.dto.UnreadMessageResponse
import com.moz1mozi.chat.message.repository.ChatMessageRepository
import com.moz1mozi.chat.room.service.ChatRoomService
import com.moz1mozi.chat.user.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CompletableFuture

@Service
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomService: ChatRoomService,
    private val userService: UserService,
) {

    val logger = KotlinLogging.logger {}

    @Async
    @Transactional
    fun saveMessage(chatMessageRequest: ChatMessageRequest): CompletableFuture<ChatMessageResponse> {
        // 🔹 이미 영속 상태인 엔터티 가져오기
        val findUser = chatMessageRequest.creator?.let { userService.findUser(it) }
            ?: throw IllegalArgumentException("User not found: ${chatMessageRequest.creator}")

        val findChatRoom = chatRoomService.findChatRoom(chatMessageRequest.chatRoomNo)

        // 🔹 `findChatRoom`을 영속 상태로 사용 (toEntity() 제거)
        val chatMessage = ChatMessage(
            msgContent = chatMessageRequest.msgContent,
            chatRoom = findChatRoom,  // ✅ 영속 상태 유지
            user = findUser.toEntity()
        ).apply { creator = chatMessageRequest.creator }

        val savedMessage = chatMessageRepository.save(chatMessage)

        logger.info { "채팅 메시지 저장 완료: ${savedMessage.id}" }

        return CompletableFuture.completedFuture(ChatMessageResponse.from(savedMessage))
    }

    @Transactional
    fun getMessage(chatRoomNo: Long, userNo: Long): List<ChatMessageResponse> {
        val chatMessages = chatMessageRepository.findAllByChatRoomId(chatRoomNo)
        chatRoomService.updateEntryDt(chatRoomNo, userNo)
        return chatMessages.map { ChatMessageResponse.from(it) }
    }

    @Transactional
    fun getUnreadMessages(userId: Long): List<UnreadMessageResponse> {
        return chatMessageRepository.selectUnreadMessages(userId)
    }


}