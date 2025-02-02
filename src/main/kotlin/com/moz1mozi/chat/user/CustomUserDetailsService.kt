package com.moz1mozi.chat.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    @Autowired private val userService: UserService
): UserDetailsService{


    override fun loadUserByUsername(username: String?): UserDetails {
        val findUser = userService.findUser(username ?: "")
        val authorities = arrayListOf<GrantedAuthority>()
        authorities.add(SimpleGrantedAuthority("ROLE_USER"))
        if (findUser != null) {
            return User(findUser.username, findUser.password, authorities)
        } else
            throw UsernameNotFoundException("User not found")
    }
}