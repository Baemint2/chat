package com.moz1mozi.chat.room.service

import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.ChatRoomMng
import com.moz1mozi.chat.entity.ChatUserPK
import com.moz1mozi.chat.message.service.ChatMessageQueryService
import com.moz1mozi.chat.room.dto.ChatRoomRequest
import com.moz1mozi.chat.room.dto.ChatRoomResponse
import com.moz1mozi.chat.room.dto.ChatRoomSearchResponse
import com.moz1mozi.chat.room.repository.ChatRoomMngRepository
import com.moz1mozi.chat.room.repository.ChatRoomRepository
import com.moz1mozi.chat.user.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(
    private val chatRoomMngRepository: ChatRoomMngRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageService: ChatMessageQueryService,
    private val userService: UserService,
) {

    // 채팅방 생성
    @Transactional
    fun createChatRoom(chatRoom: ChatRoomRequest, creator: String, usernameList: List<String>): ChatRoomResponse {
        val savedChatRoom = chatRoomRepository.save(chatRoom.toEntity())
        savedChatRoom.creator = creator

        val findChatRoom = chatRoomRepository.findById(savedChatRoom.id!!).orElseThrow()
        val chatRoomMngList = usernameList.map { username ->
            val findUser = userService.findUserByNickname(username) ?: throw IllegalArgumentException("User not found: $username")
            val chatUserPK = ChatUserPK(chatRoom = findChatRoom, user = findUser.toEntity())
            ChatRoomMng(chatUserPK)
        }

        chatRoomMngRepository.saveAll(chatRoomMngList)

        return ChatRoomResponse.from(savedChatRoom)
    }

    // 채팅방 상세 조회
    @Transactional
    fun findChatRoomByUsername(username: String): List<ChatRoomSearchResponse> {
        val selectChatRoom = chatRoomRepository.selectChatRoom(username)
        selectChatRoom.map { chatRoom ->
            val findLatelyMessage = chatMessageService.findLatelyMessage(chatRoom.chatRoomId)
            chatRoom.latelyMessage = findLatelyMessage?.msgContent
        }
        return selectChatRoom
    }

    // 채팅방 조회
    @Transactional
    fun findChatRoom(chatRoomId: Long): ChatRoom {
        val findById = chatRoomRepository.findById(chatRoomId).orElseThrow()
        return findById
    }

    // 채팅방 접속시간 업데이트
    @Transactional
    fun updateEntryDt(chatRoomNo: Long, userNo:Long) {
        chatRoomMngRepository.updateEntryDt(chatRoomNo, userNo)
    }

    @Transactional
    fun getParticipants(chatRoomId: Long): List<Long> {
        return chatRoomMngRepository.findParticipants(chatRoomId)
    }
}