package com.moz1mozi.chat.user.repository

import com.moz1mozi.chat.entity.User

interface UserCustomRepository {

    fun searchUsers(searchText: String): List<User>
}