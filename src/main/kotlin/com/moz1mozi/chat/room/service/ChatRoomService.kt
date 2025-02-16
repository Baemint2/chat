package com.moz1mozi.chat.room.service

import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.ChatRoomMng
import com.moz1mozi.chat.entity.ChatUserPK
import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.room.dto.ChatRoomRequest
import com.moz1mozi.chat.room.dto.ChatRoomResponse
import com.moz1mozi.chat.room.dto.ChatRoomSearchResponse
import com.moz1mozi.chat.room.repository.ChatRoomMngRepository
import com.moz1mozi.chat.room.repository.ChatRoomRepository
import com.moz1mozi.chat.user.UserService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(
    private val chatRoomMngRepository: ChatRoomMngRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val userService: UserService,
) {
    private val logger = KotlinLogging.logger {}

    // 채팅방 생성
    @Transactional
    fun createChatRoom(chatRoom: ChatRoomRequest, creator: String, usernameList: List<String>): ChatRoomResponse {
        val savedChatRoom = chatRoomRepository.save(chatRoom.toEntity().apply { this.creator = creator })

        val chatRoomMngList = usernameList.map { username ->
            createChatRoomMng(username, savedChatRoom)
        }

        chatRoomMngRepository.saveAll(chatRoomMngList)

        return ChatRoomResponse.from(savedChatRoom)
    }

    private fun createChatRoomMng(username: String, savedChatRoom: ChatRoom): ChatRoomMng {
        val findUser = userService.findUserByNickname(username)
            ?: throw IllegalArgumentException("User not found: $username")
        val chatUserPK = ChatUserPK(chatRoom = savedChatRoom, user = findUser.toEntity())
        return ChatRoomMng(chatUserPK)
    }

    // 로그인한 유저의 채팅방 목록 조회
    @Transactional
    fun findChatRoomByUsername(username: String): List<ChatRoomSearchResponse> {
        val selectChatRoom = chatRoomRepository.selectChatRoom(username)
        return selectChatRoom
    }

    @Transactional
    fun findChatRoomByUsername(username: String, sender: String, senderChatRoomId: Long): List<ChatRoomSearchResponse> {
        return chatRoomRepository.selectChatRoom(username).map { chatRoom ->
            if (username == sender && chatRoom.chatRoomId == senderChatRoomId) {
                chatRoom.unreadCount = 0
            }
            chatRoom
        }
    }

    // 채팅방 접속
    @Transactional
    fun findChatRoom(chatRoomId: Long): ChatRoom {
        return chatRoomRepository.findById(chatRoomId).orElseThrow()
    }

    // 채팅방 접속시간 업데이트
    @Transactional
    fun updateEntryDt(chatRoomId: Long, username: String) {
        val findUser = userService.findUser(username) ?: throw IllegalArgumentException("User not found: $username")

        chatRoomMngRepository.updateEntryDt(chatRoomId, findUser.id!!)
    }

    // 채팅방 마지막 접속시간 업데이트
    @Transactional
    fun updateLastSeenDt(chatRoomId: Long, username: String) {
        val findUser = userService.findUser(username) ?: throw IllegalArgumentException("User not found: $username")

        chatRoomMngRepository.updateLastSeenDt(chatRoomId, findUser.id!!)
    }

    // 채팅방에 접속해있는 유저들의 리스트를 채팅방 별로 조회한다.
    @Transactional
    fun getParticipants(chatRoomId: Long): List<String> {
        return chatRoomMngRepository.findParticipants(chatRoomId)
    }

    // 채팅방 상태 업데이트
    @Transactional
    fun updateEntryStat(chatRoomId: Long, username: String, status: Status): Int {
        val findUser = userService.findUser(username) ?: throw IllegalArgumentException("User not found: $username")
        return chatRoomMngRepository.updateEntryStat(chatRoomId, findUser.id!!, status)
    }

    // 채팅방 초대하기
    @Transactional
    fun inviteChatRoom(chatRoomId: Long, username: String) {

        val findChatRoom = findChatRoom(chatRoomId)
        val findUser = userService.findUser(username) ?: throw IllegalArgumentException("User not found: $username")

        val existUser = chatRoomMngRepository.existUser(chatRoomId, findUser.id!!)
        if (existUser) {
            updateEntryDt(chatRoomId, findUser.username)
        } else {
            val chatUserPK = ChatUserPK(findChatRoom, findUser.toEntity())
            val chatRoomMng = ChatRoomMng(chatUserPK)
            chatRoomMngRepository.save(chatRoomMng)
        }

//        messagingTemplate?.convertAndSend("/")
    }
}