package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.ChatRoomMng
import com.moz1mozi.chat.entity.ChatUserPK
import com.moz1mozi.chat.message.dto.ChatRoomRequest
import com.moz1mozi.chat.message.dto.ChatRoomResponse
import com.moz1mozi.chat.message.dto.ChatRoomSearchResponse
import com.moz1mozi.chat.message.repository.ChatRoomMngRepository
import com.moz1mozi.chat.message.repository.ChatRoomRepository
import com.moz1mozi.chat.user.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChatRoomService(
    private val chatRoomMngRepository: ChatRoomMngRepository,
    private val chatRoomRepository: ChatRoomRepository,
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

    @Transactional
    fun createChatRoomMng(username: String, chatRoomId: Long): ChatRoomMng {
        val findUser = userService.findUser(username);
        val findChatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow()
        val chatUserPK = ChatUserPK(chatRoom = findChatRoom, user = findUser!!.toEntity())
        val chatRoomMng = ChatRoomMng(chatUserPK)
        return chatRoomMngRepository.save(chatRoomMng)
    }

    // 채팅방 조회
    @Transactional
    fun getChatRoom(username: String): List<ChatRoomSearchResponse> {
        val selectChatRoom = chatRoomRepository.selectChatRoom(username)
        return selectChatRoom
    }
}