package com.moz1mozi.chat.user

import com.moz1mozi.chat.user.dto.UserResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class UserController(
    private val userService: UserService
) {
    private val logger = KotlinLogging.logger { }

    // 로그인한 유저 조회
    @GetMapping("/userInfo/{username}")
    fun getUser(@PathVariable username: String): ResponseEntity<Pair<String, UserResponse?>> {

        val findUser = userService.findUser(username)
        logger.info { "Found user: $findUser" }
        return ResponseEntity.ok("userInfo" to findUser)
    }

    // 로그인한 유저를 제외한 유저리스트 조회
    @GetMapping("/users")
    fun getUsers(servletRequest: HttpServletRequest): ResponseEntity<out Map<String, List<Any>>> {
        val cookies = servletRequest.cookies ?: emptyArray()

        val loginUser = cookies.find { it.name == "username" }?.value
            ?: return ResponseEntity.badRequest().body(mapOf("error" to listOf("Username cookie not found")))

        val findAllUsers = userService.findAllUsers(loginUser)

        return ResponseEntity.ok(mapOf("users" to findAllUsers))
    }

    // 채팅방에 속해있는 유저를 제외하고 조회
    @GetMapping("/users/exclude/{chatRoomId}")
    fun getUserByChatRoomId(@PathVariable chatRoomId: Long): ResponseEntity<out Map<String, List<UserResponse>>> {
        val findUsersNotInChatRoom = userService.findUsersNotInChatRoom(chatRoomId)
        return ResponseEntity.ok(mapOf("users" to findUsersNotInChatRoom))
    }


    // 검색 조건에 맞는 유저 리스트조회
    @GetMapping("/users/search/{searchText}")
    fun searchUsers(@PathVariable searchText: String): ResponseEntity<List<UserResponse>> {
        return ResponseEntity.ok(userService.searchUsers(searchText))
    }


}