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
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(
    private val chatRoomMngRepository: ChatRoomMngRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageService: ChatMessageQueryService,
    private val userService: UserService,
) {
    private val logger = KotlinLogging.logger {}

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

        return ChatRoomResponse.Companion.from(savedChatRoom)
    }

    // 로그인한 유저의 채팅방 목록 조회
    @Transactional
    fun findChatRoomByUsername(username: String): List<ChatRoomSearchResponse> {
        val selectChatRoom = chatRoomRepository.selectChatRoom(username)
        selectChatRoom.map { chatRoom ->
            val findLatelyMessage = chatMessageService.findLatelyMessage(chatRoom.chatRoomId)
            chatRoom.latelyMessage = findLatelyMessage?.msgContent
        }
        return selectChatRoom
    }

    // 채팅방 접속
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

    // 채팅방에 접속해있는 유저들의 리스트를 채팅방 별로 조회한다.
    @Transactional
    fun getParticipants(chatRoomId: Long): List<String> {
        return chatRoomMngRepository.findParticipants(chatRoomId)
    }
}