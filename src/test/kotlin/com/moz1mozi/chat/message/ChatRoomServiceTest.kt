package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.ChatRoomMng
import com.moz1mozi.chat.entity.ChatUserPK
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.message.repository.ChatRoomMngRepository
import com.moz1mozi.chat.message.repository.ChatRoomRepository
import com.moz1mozi.chat.user.UserService
import com.moz1mozi.chat.user.dto.UserResponse
import com.moz1mozi.chat.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.InjectMocks
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.util.*

@SpringBootTest
class ChatRoomServiceTest @Autowired constructor(
 @InjectMocks val chatRoomService: ChatRoomService,
 @MockitoBean val chatRoomRepository: ChatRoomRepository,
 @MockitoBean val userRepository: UserRepository,
 @MockitoBean val userService: UserService,
 @MockitoBean val chatRoomMngRepository: ChatRoomMngRepository,
 val passwordEncoder: PasswordEncoder,
 ) {

 private val logger = KotlinLogging.logger { }

 lateinit var chatRoom: ChatRoom
 lateinit var user: User
 lateinit var userResponse: UserResponse
 lateinit var chatRoomMng: ChatRoomMng
 @BeforeEach
 fun setUp() {
  chatRoom = ChatRoom(
   chatRoomTitle = "테스트 채팅방"
  ).apply {
   id = 99L
  }

  user = User(
   username = "testUsername",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname",
  ).apply { id = 99L }

  userResponse = UserResponse(
   username = "testUsername",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname",
  )

  val chatUserPk = ChatUserPK(chatRoom, user)
  chatRoomMng = ChatRoomMng(chatUserPk = chatUserPk)

 }

@Test
 fun createChatRoom() {
  `when`(chatRoomRepository.save(any())).thenReturn(chatRoom)
  val createChatRoom = chatRoomService.createChatRoom(chatRoom)
  assertEquals(chatRoom.id, createChatRoom.id)
}

 @Test
 fun createChatRoomMng() {
  `when`(userRepository.findByUsername("testUsername")).thenReturn(user)
  `when`(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(chatRoom))
  `when`(userService.findUser(anyString())).thenReturn(userResponse)
  `when`(chatRoomMngRepository.save(any())).thenReturn(chatRoomMng)

  val findUser = userService.findUser(user.username)
  val createChatRoomMng = chatRoomService.createChatRoomMng(findUser!!.username, anyLong())
  logger.info{"createChatRoomMng: ${createChatRoomMng.chatUserPk.user.id}, ${createChatRoomMng.chatUserPk.chatRoom.id}, ${createChatRoomMng.entryStat}"}
 }
}