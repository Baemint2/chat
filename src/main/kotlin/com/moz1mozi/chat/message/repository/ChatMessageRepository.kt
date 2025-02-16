package com.moz1mozi.chat.message.repository

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.entity.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface ChatMessageRepository: JpaRepository<ChatMessage, Long>, ChatMessageCustomRepository {
    fun findAllByChatRoomId(chatRoomId: Long): List<ChatMessage>

    @Query("select c " +
            " from ChatMessage c " +
            "where c.chatRoom.id = :chatRoomId " +
            "order by c.msgDt desc " +
            "limit 1")
    fun selectLatelyMessage(chatRoomId: Long?): ChatMessage?

    @Modifying(clearAutomatically = true)
    @Query("update ChatMessage cm" +
            "   set cm.msgStat = :status " +
            " where cm.chatRoom.id = :chatRoomId " +
            "   and cm.user.id = :userId" +
            "   and cm.id = :msgId")
    fun updateMsgStat(@Param("chatRoomId") chatRoomId: Long,
                        @Param("userId") userId: Long,
                        @Param("msgId") msgId: Long,
                        @Param("status") status: Status): Int

    override fun findById(cm: Long): Optional<ChatMessage?>
}