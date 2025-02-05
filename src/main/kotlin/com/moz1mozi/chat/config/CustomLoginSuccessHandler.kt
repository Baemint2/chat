package com.moz1mozi.chat.config

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

class CustomLoginSuccessHandler(): AuthenticationSuccessHandler {

    private val log = KotlinLogging.logger { }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authentication: Authentication?
    ) {
        // 쿠키에 새 토큰 저장
        val accessTokenCookie = Cookie("username", authentication?.name)
        accessTokenCookie.path = "/"
        accessTokenCookie.isHttpOnly = false
        accessTokenCookie.maxAge = 86400
        response!!.addCookie(accessTokenCookie)
        SecurityContextHolder.getContext().authentication = authentication
        response!!.sendRedirect("http://localhost:3000/chat")
    }
}