package com.moz1mozi.chat.room.repository

import com.moz1mozi.chat.entity.ChatRoomMng
import com.moz1mozi.chat.entity.ChatUserPK
import com.moz1mozi.chat.entity.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ChatRoomMngRepository: JpaRepository<ChatRoomMng, ChatUserPK> {

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

    @Modifying(clearAutomatically = true)
    @Query("update ChatRoomMng " +
            "   set entryStat = :status " +
            " where chatUserPk.chatRoom.id = :chatRoomId " +
            "   and chatUserPk.user.id = :userId")
    fun updateEntryStat(@Param("chatRoomId") chatRoomId: Long,
                        @Param("userId") userId: Long,
                        @Param("status") status: Status
    )

}