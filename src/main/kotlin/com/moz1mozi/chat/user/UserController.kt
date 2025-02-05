package com.moz1mozi.chat.user

import com.moz1mozi.chat.user.dto.UserResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
class UserController(
    private val userService: UserService
) {
    private val logger = KotlinLogging.logger { }

    @GetMapping("/userInfo/{username}")
    fun getUser(@PathVariable username: String): ResponseEntity<Pair<String, UserResponse?>> {

        val findUser = userService.findUser(username)
        logger.info { "Found user: $findUser" }
        return ResponseEntity.ok("userInfo" to findUser)
    }
}