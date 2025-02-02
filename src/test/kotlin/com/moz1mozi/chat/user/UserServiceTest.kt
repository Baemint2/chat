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
 @BeforeEach
 fun setup() {
  user = User(
   username = "testUser",
   password = passwordEncoder.encode("1234"),
   nickname = "testNickname",
  )
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

}