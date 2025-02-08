package com.moz1mozi.chat.user

import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.user.dto.UserResponse
import com.moz1mozi.chat.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@SpringBootTest
class UserServiceTest @Autowired constructor(

 @InjectMocks
 val userService: UserService,

 @MockitoBean val userRepository: UserRepository,
 val passwordEncoder: PasswordEncoder,
) {
 private val logger = KotlinLogging.logger {}

 lateinit var user: User
 lateinit var user2: User
 lateinit var user3: User
 val userList: MutableList<User> = mutableListOf()
 @BeforeEach
 fun setup() {
  user = User(
   username = "testUsername",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname",
  )

  user2 = User(
   username = "testUsername2",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname2",
  )

  user3 = User(
   username = "moz1mozi",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname3",
  )

  userList.add(user)
  userList.add(user2)
  userList.add(user3)
 }

  @Test
  @Transactional
  @DisplayName("유저 회원가입")
  fun 유저회원가입() {
   `when`(userRepository.save(any())).thenReturn(user)
   val saveUser = userService.saveUser(user)
   logger.info { saveUser }
  }

 @Test
 @Transactional
 @DisplayName("유저네임이 이미 등록되어있는지 확인")
 fun 중복유저네임검증() {
  `when`(userRepository.save(user)).thenReturn(user)
  val saveUser = userService.saveUser(user)
  logger.info { saveUser }

  `when`(userRepository.findByUsername(user.username)).thenReturn(user)
  val exception = assertThrows(IllegalArgumentException::class.java) {
   userService.saveUser(user)
  }
  logger.info { exception.message }
 }

 @Test
 @DisplayName("아이디로 유저찾기 성공")
 fun 아이디로_유저찾기_성공() {
  `when`(userRepository.findByUsername("testUsername")).thenReturn(user)
  val findUser = userService.findUser(user.username)
  logger.info { findUser }
  assertNotNull(findUser)
 }

 @Test
 @DisplayName("아이디로 유저찾기 실패")
 fun 아이디로_유저찾기_실패() {
  `when`(userRepository.findByUsername(user.username)).thenReturn(null)
  val exception = assertThrows(IllegalArgumentException::class.java) {
   userService.findUser(user.username)
  }
  logger.info { exception.message }
 }

 @Test
 @DisplayName("로그인한 본인을 제외한 전체 유저리스트 조회하기")
 fun 유저전체조회() {
  `when`(userRepository.findAll()).thenReturn(userList)
  val findAllUsers = userService.findAllUsers(user.username)
  findAllUsers.forEach { user -> logger.info { user.username }}
 }

 @Test
 @DisplayName("특정 텍스트에 맞는 유저리스트 조회하기")
 fun 텍스트로유저조회() {
  `when`(userRepository.searchUsers("t")).thenReturn(userList)
  val searchUsers = userService.searchUsers("t")
  searchUsers.forEach { user -> logger.info { user.username } }

 }

 @Test
 @DisplayName("채팅방에 속해있는 유저를 제외하고 조회")
 fun findUsersNotInChatRoom() {
  `when`(userRepository.selectUsersNotInChatRoom(anyLong())).thenReturn(listOf(user, user2))
  val findUsersNotInChatRoom = userService.findUsersNotInChatRoom(35L)
  logger.info { "findUsersNotInChatRoom: $findUsersNotInChatRoom" }

 }

}