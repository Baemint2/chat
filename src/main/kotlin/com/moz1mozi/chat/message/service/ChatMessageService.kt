package com.moz1mozi.chat.message.service

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.message.dto.ChatMessageRequest
import com.moz1mozi.chat.message.dto.ChatMessageResponse
import com.moz1mozi.chat.message.dto.UnreadMessageResponse
import com.moz1mozi.chat.message.repository.ChatMessageRepository
import com.moz1mozi.chat.room.service.ChatRoomService
import com.moz1mozi.chat.user.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatMessageService(
    private val chatMessageRepository: ChatMessageRepository,
    private val chatRoomService: ChatRoomService,
    private val userService: UserService,
    private val messagingTemplate: SimpMessageSendingOperations? = null
) {

    val logger = KotlinLogging.logger {}

    @Transactional
    fun saveMessage(chatMessageRequest: ChatMessageRequest): ChatMessageResponse {
        val findUser = chatMessageRequest.creator?.let { userService.findUser(it) }
            ?: throw IllegalArgumentException("User not found: ${chatMessageRequest.creator}")

        val findChatRoom = chatRoomService.findChatRoom(chatMessageRequest.chatRoomId)

        val chatMessage = ChatMessage(
            msgContent = chatMessageRequest.msgContent,
            chatRoom = findChatRoom,  // ✅ 영속 상태 유지
            user = findUser.toEntity()
        ).apply { creator = chatMessageRequest.creator }

        val savedMessage = chatMessageRepository.save(chatMessage)

        logger.info { "채팅 메시지 저장 완료: ${savedMessage.id}" }

        return ChatMessageResponse.from(savedMessage)
    }

    @Transactional
    fun saveAndNotifyParticipants(chatMessage: ChatMessageRequest) {
        try {
            logger.info { "Sending message ${chatMessage.toString()}" }
            val saveMessage = saveMessage(chatMessage)
            messagingTemplate?.convertAndSend(
                "/sub/chat/room/" + chatMessage.chatRoomId,
                saveMessage
            )

            // 채팅방에 속해있는 참가자들 조회
            val participants = chatRoomService.getParticipants(chatMessage.chatRoomId)

            participants.forEach { user ->
                chatRoomService.updateEntryStat(chatMessage.chatRoomId, user, Status.ENABLED)

                val findChatRoomByUsername =
                    chatRoomService.findChatRoomByUsername(user, chatMessage.creator!!, chatMessage.chatRoomId)
                logger.info { "유저 ${user}에게 채팅방 목록 업데이트 전송" }
                messagingTemplate?.convertAndSend(
                    "/sub/chat/update/$user",
                    findChatRoomByUsername
                )
            }
        } catch (e: Exception) {
            logger.error { e.message }
        }
    }

    @Transactional
    fun getMessage(chatRoomNo: Long, pageable: Pageable): Slice<ChatMessageResponse> {
        val chatMessages = chatMessageRepository.selectMessage(chatRoomNo, pageable)
        logger.info { "chatMessages $chatMessages" }
        val sortedBy = chatMessages.content.sortedBy { it.msgDt }
        return SliceImpl(sortedBy.map { ChatMessageResponse.from(it) }, pageable, chatMessages.hasNext())
    }

    @Transactional
    fun getUnreadMessages(userId: Long): List<UnreadMessageResponse> {
        return chatMessageRepository.selectUnreadMessages(userId)
    }

    @Transactional
    fun updateMsgStat(chatRoomId: Long, userId: Long, msgId: Long): ChatMessageResponse {
        val updateMsgStat = chatMessageRepository.updateMsgStat(chatRoomId, userId, msgId, Status.DISABLED)
        val findById = chatMessageRepository.findById(msgId).get()
        return ChatMessageResponse.from(findById)
    }


}