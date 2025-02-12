package com.moz1mozi.chat.message.repository.impl

import com.moz1mozi.chat.entity.QChatMessage.chatMessage
import com.moz1mozi.chat.entity.QChatRoomMng.chatRoomMng
import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.message.dto.UnreadMessageResponse
import com.moz1mozi.chat.message.repository.ChatMessageCustomRepository
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import java.time.LocalDateTime

class ChatMessageCustomRepositoryImpl(
    entityManager: EntityManager
): ChatMessageCustomRepository {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    override fun selectUnreadMessages(userId: Long): List<UnreadMessageResponse> {

        val unreadCountExpression = Expressions.numberPath(Long::class.java, "unreadCount")

        val results = queryFactory
            .select(
                chatRoomMng.chatUserPk.chatRoom.id,
                chatRoomMng.chatUserPk.user.id,
                chatMessage.count().`as`(unreadCountExpression)
            )
            .from(chatRoomMng)
            .join(chatMessage)
            .on(chatRoomMng.chatUserPk.chatRoom.id.eq(chatMessage.chatRoom.id))
            .where(chatRoomMng.chatUserPk.user.id.eq(userId))
            .where(chatRoomMng.entryDt.before(LocalDateTime.now()))
            .where(chatMessage.msgDt.gt(chatRoomMng.entryDt))
            .where(chatRoomMng.entryStat.eq(Status.ENABLED))
            .groupBy(chatRoomMng.chatUserPk.chatRoom.id)
            .fetch()
        return results.map { tuple ->
            UnreadMessageResponse(
                chatRoomId = tuple.get(chatRoomMng.chatUserPk.chatRoom.id)!!,
                userId = tuple.get(chatRoomMng.chatUserPk.user.id)!!,
                unreadCount = tuple.get(unreadCountExpression) ?: 0,
            )
        };
    }
}