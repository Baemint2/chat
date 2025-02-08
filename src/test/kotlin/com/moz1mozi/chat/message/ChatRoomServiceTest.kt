package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.ChatRoomMng
import com.moz1mozi.chat.entity.ChatUserPK
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.message.dto.ChatRoomRequest
import com.moz1mozi.chat.message.dto.ChatRoomSearchResponse
import com.moz1mozi.chat.message.repository.ChatRoomMngRepository
import com.moz1mozi.chat.message.repository.ChatRoomRepository
import com.moz1mozi.chat.user.UserService
import com.moz1mozi.chat.user.dto.UserInfo
import com.moz1mozi.chat.user.dto.UserResponse
import com.moz1mozi.chat.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDateTime
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
 lateinit var chatRoom2: ChatRoom
 lateinit var user: User
 lateinit var user2: User
 lateinit var userResponse: UserResponse
 lateinit var userResponse2: UserResponse
 lateinit var chatRoomMng: ChatRoomMng
 lateinit var chatRoomSearchResponse: ChatRoomSearchResponse
 lateinit var chatRoomSearchResponse2: ChatRoomSearchResponse
 var chatRooms: MutableList<ChatRoom> = mutableListOf()
 var chatRoomSearchRespons: MutableList<ChatRoomSearchResponse> = mutableListOf()
 var participantUsers: List<String> = mutableListOf();
 @BeforeEach
 fun setUp() {

  chatRoom = ChatRoom(
   chatRoomTitle = "테스트 채팅방"
  ).apply {
   creator = "testUser"
   id = 99L
  }
  chatRoom2 = ChatRoom(
   chatRoomTitle = "테스트 채팅방2"
  ).apply {
   creator = "testUser2"
   id = 100L
  }

  chatRooms.add(chatRoom)
  chatRooms.add(chatRoom2)

  user = User(
   username = "testUsername",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname",
  ).apply { id = 99L }

  user2 = User(
   username = "testUsername2",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname",
  ).apply { id = 100L }

  userResponse = UserResponse(
      id = null,
      username = "testUsername",
      password = passwordEncoder.encode("1234"),
      nickname = "testNickname",
  )
  userResponse2 = UserResponse(
   id = null,
   username = "testUsername2",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname2",
  )

  val chatUserPk = ChatUserPK(chatRoom, user)
  chatRoomMng = ChatRoomMng(chatUserPk = chatUserPk)

  chatRoomSearchResponse = ChatRoomSearchResponse(
   chatRoomId = 99L,
   chatRoomTitle = "테스트 채팅방1",
   creator = "testUser1",
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now(),
   participantUsers = listOf(UserInfo(user.username, user.nickname!!)),
  )

  chatRoomSearchResponse2 = ChatRoomSearchResponse(
   chatRoomId = 100L,
   chatRoomTitle = "테스트 채팅방2",
   creator = "testUser1",
   createdAt = LocalDateTime.now(),
   updatedAt = LocalDateTime.now(),
   participantUsers = listOf(UserInfo(user.username, user.nickname!!), UserInfo(user2.username, user2.nickname!!)),
  )

  chatRoomSearchRespons.add(chatRoomSearchResponse)
  chatRoomSearchRespons.add(chatRoomSearchResponse2)

 }

@Test
@DisplayName("채팅방을 생성한다.")
 fun 채팅방_생성() {
  `when`(chatRoomRepository.save(any<ChatRoom>())).thenReturn(chatRoom)
  val createChatRoom = chatRoomService.createChatRoom(ChatRoomRequest.of(chatRoom), "testUser", listOf("testUser", "testUser2"))
  assertEquals(chatRoom.id, createChatRoom.id)
  logger.info { "${createChatRoom.id}, ${createChatRoom.chatRoomTitle}, ${createChatRoom.creator}" }
}

 @Test
 @DisplayName("채팅관리테이블의 데이터를 생성한다.")
 fun 채팅_관리_생성() {
  `when`(userRepository.findByUsername("testUsername")).thenReturn(user)
  `when`(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(chatRoom))
  `when`(userService.findUser(anyString())).thenReturn(userResponse)
  `when`(chatRoomMngRepository.save(any())).thenReturn(chatRoomMng)

  val findUser = userService.findUser(user.username)
  val createChatRoomMng = chatRoomService.createChatRoomMng(findUser!!.username, anyLong())
  logger.info{"createChatRoomMng: ${createChatRoomMng.chatUserPk.user.id}, ${createChatRoomMng.chatUserPk.chatRoom.id}, ${createChatRoomMng.entryStat}"}
 }

 @Test
 @DisplayName("특정 유저의 활성화된 채팅방 목록을 조회한다.")
 fun 채팅방_목록_조회() {
  `when`(userRepository.findByUsername("testUsername")).thenReturn(user)
  `when`(chatRoomRepository.selectChatRoom("testUsername")).thenReturn(chatRoomSearchRespons)
  `when`(userService.findUser(anyString())).thenReturn(userResponse)
  val chatRooms = chatRoomService.getChatRoom("testUsername")

  chatRooms.forEach { chatRoom ->
   logger.info { "chatRoom: $chatRoom" }
  }
 }
}