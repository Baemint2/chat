package com.moz1mozi.chat.message

import com.moz1mozi.chat.entity.*
import com.moz1mozi.chat.message.dto.UnreadMessageResponse
import com.moz1mozi.chat.message.service.ChatMessageQueryService
import com.moz1mozi.chat.message.service.ChatMessageService
import com.moz1mozi.chat.room.dto.ChatRoomRequest
import com.moz1mozi.chat.room.dto.ChatRoomSearchResponse
import com.moz1mozi.chat.room.repository.ChatRoomMngRepository
import com.moz1mozi.chat.room.repository.ChatRoomRepository
import com.moz1mozi.chat.room.service.ChatRoomService
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
 @MockitoBean val chatMessageService: ChatMessageQueryService,
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
   creator = "testUsername"
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
   nickname = "testNickname2",
  ).apply { id = 100L }

  userResponse = UserResponse(
      id = 7L,
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
 `when`(userRepository.findByUsername("testUsername")).thenReturn(user)
 `when`(userService.findUserByNickname(anyString())).thenReturn(userResponse)

 `when`(chatRoomRepository.save(any<ChatRoom>())).thenReturn(chatRoom)
 `when`(chatRoomRepository.findById(anyLong())).thenReturn(Optional.of(chatRoom))
 val chatUserPK1 = ChatUserPK(chatRoom = chatRoom, user = userResponse.toEntity())
 val chatUserPK2 = ChatUserPK(chatRoom = chatRoom, user = userResponse2.toEntity())
 val chatRoomMng1 = ChatRoomMng(chatUserPK1)
 val chatRoomMng2 = ChatRoomMng(chatUserPK2)

 `when`(chatRoomMngRepository.saveAll(anyList())).thenReturn(listOf(chatRoomMng1, chatRoomMng2))


  val createChatRoom = chatRoomService.createChatRoom(ChatRoomRequest.of(chatRoom), anyString(), listOf("testNickname", "testNickname2"))

  assertEquals(chatRoom.id, createChatRoom.id)
  logger.info { "${createChatRoom.id}, ${createChatRoom.chatRoomTitle}, ${createChatRoom.creator}" }
}

 @Test
 @DisplayName("특정 유저의 활성화된 채팅방 목록을 조회한다.")
 fun 채팅방_목록_조회() {
  `when`(userRepository.findByUsername("testUsername")).thenReturn(user)
  `when`(chatRoomRepository.selectChatRoom("testUsername")).thenReturn(chatRoomSearchRespons)
  `when`(chatMessageService.findLatelyMessage(chatRoom.id!!)).thenReturn(ChatMessage("안녕하세요?", chatRoom, user))
  `when`(chatMessageService.getUnreadMessage(anyLong(), anyLong())).thenReturn(UnreadMessageResponse(chatRoomId = 17L, userId = userResponse.id!!, unreadCount = 1))
  `when`(userService.findUser(anyString())).thenReturn(userResponse)
  val chatRooms = chatRoomService.findChatRoomByUsername("testUsername")

  chatRooms.forEach { chatRoom ->
   logger.info { "chatRoom: $chatRoom" }
  }
 }
}