package com.moz1mozi.chat.message

import com.fasterxml.jackson.databind.ObjectMapper
import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.message.dto.ChatRoomRequest
import com.moz1mozi.chat.message.dto.ChatRoomResponse
import com.moz1mozi.chat.message.repository.ChatRoomMngRepository
import com.moz1mozi.chat.message.repository.ChatRoomRepository
import com.moz1mozi.chat.user.UserService
import com.moz1mozi.chat.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mockito.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@MockitoBean(types = [JpaMetamodelMappingContext::class])
class ChatRoomControllerTest @Autowired constructor(
 @InjectMocks val chatRoomService: ChatRoomService,
 @MockitoBean val chatRoomRepository: ChatRoomRepository,
 @MockitoBean val userRepository: UserRepository,
 @MockitoBean val userService: UserService,
 @MockitoBean val chatRoomMngRepository: ChatRoomMngRepository,
) {
 var logger = KotlinLogging.logger {}

 @Autowired
 private lateinit var objectMapper: ObjectMapper

 lateinit var mockMvc: MockMvc
 lateinit var chatRoom: ChatRoom


 @BeforeEach
 fun setUp(webApplicationContext: WebApplicationContext) {
  this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
   .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
   .build()
  chatRoom = ChatRoom(
   chatRoomTitle = "테스트 채팅방"
  ).apply {
   creator = "testUser"
   id = 99L
  }
 }

  @Test
  @WithMockUser(username = "testUser")
  fun createChatRoom() {
   val chatRoomResponse = ChatRoomResponse(99L, "기본 채팅방", "testUser")
   val chatRoomRequest = ChatRoomRequest(chatRoomTitle = "기본 채팅방")
   val chatRoomEntity = ChatRoom(chatRoomTitle = "기본 채팅방").apply { creator = "testUser" }
   `when`(chatRoomRepository.save(any<ChatRoom>())).thenReturn(chatRoom)
   `when`(chatRoomService.createChatRoom(chatRoomRequest, "testUser")).thenReturn(chatRoomResponse)
   mockMvc.perform(post("/chatRoom")
    .with(csrf())
    .contentType(MediaType.APPLICATION_JSON)
    .content(objectMapper.writeValueAsString(chatRoomRequest)))
    .andExpect(status().isOk)
    .andDo { result -> logger.info {result.response.contentAsString} }
  }
}