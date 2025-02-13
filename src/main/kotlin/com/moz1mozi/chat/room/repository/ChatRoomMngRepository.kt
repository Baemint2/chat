package com.moz1mozi.chat.room.repository

import com.moz1mozi.chat.entity.ChatRoomMng
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ChatRoomMngRepository: JpaRepository<ChatRoomMng, Long> {

    @Modifying
    @Query("update ChatRoomMng " +
            " set entryDt = now()" +
            " where chatUserPk.chatRoom.id = :chatRoomId " +
            " and chatUserPk.user.id = :userId")
    fun updateEntryDt(chatRoomId: Long, userId: Long): Int

    @Modifying
    @Query("update ChatRoomMng " +
            " set lastSeenDt = now()" +
            " where chatUserPk.chatRoom.id = :chatRoomId " +
            " and chatUserPk.user.id = :userId")
    fun updateLastSeenDt(chatRoomId: Long, userId: Long): Int

    @Query("SELECT crm.chatUserPk.user.username " +
            "FROM ChatRoomMng crm " +
            "WHERE crm.chatUserPk.chatRoom.id = :chatRoomId")
    fun findParticipants(chatRoomId: Long): List<String>

}