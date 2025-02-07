package com.moz1mozi.chat.user

import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.user.dto.UserResponse
import com.moz1mozi.chat.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import kotlin.test.Test

@SpringBootTest
@MockitoBean(types = [JpaMetamodelMappingContext::class])
class UserControllerTest @Autowired constructor(
 @MockitoBean private val userService: UserService,
 val passwordEncoder: PasswordEncoder,
) {
 var logger = KotlinLogging.logger {}
 lateinit var mockMvc: MockMvc

 lateinit var user: UserResponse
 lateinit var user2: UserResponse
 lateinit var user3: UserResponse
 val userList: MutableList<UserResponse> = mutableListOf()

 @BeforeEach
 fun setUp(webApplicationContext: WebApplicationContext) {
  this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
   .apply<DefaultMockMvcBuilder>(SecurityMockMvcConfigurers.springSecurity())
   .build()

  user = UserResponse(
   username = "testUsername",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname",
  )

  user2 = UserResponse(
   username = "testUsername2",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname2",
  )

  user3 = UserResponse(
   username = "moz1mozi",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname3",
  )

  userList.add(user)
  userList.add(user2)
  userList.add(user3)
 }

 @Test
 fun getUser() {
  `when`(userService.findUser(anyString())).thenReturn(UserResponse(username = "testUser", password = "testPassword", nickname = "testNickname"))
  mockMvc.perform(get("/userInfo/{username}", "testUser")
   .with(csrf())
   .contentType(MediaType.APPLICATION_JSON))
   .andExpect { result -> logger.info { "Found user: ${result.response.contentAsString}" } }
 }

 @Test
 fun getUsers() {
  `when`(userService.findAllUsers()).thenReturn((userList))
  mockMvc.perform(get("/users")
   .with(csrf())
   .contentType(MediaType.APPLICATION_JSON))
   .andExpect { result -> logger.info { "Found users: ${result.response.contentAsString}" } }
 }

 @Test
 fun searchUsers() {
  `when`(userService.searchUsers("m")).thenReturn((listOf(user3)))
  mockMvc.perform(get("/users/{searchText}", "m" )
   .with(csrf())
   .contentType(MediaType.APPLICATION_JSON))
   .andExpect { result -> logger.info { "Found users: ${result.response.contentAsString}" } }
 }

}