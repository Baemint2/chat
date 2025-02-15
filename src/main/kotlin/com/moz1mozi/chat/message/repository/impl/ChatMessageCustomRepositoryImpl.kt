package com.moz1mozi.chat.message.repository.impl

import com.moz1mozi.chat.entity.ChatMessage
import com.moz1mozi.chat.entity.QChatMessage.chatMessage
import com.moz1mozi.chat.entity.QChatRoomMng.chatRoomMng
import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.message.dto.UnreadMessageResponse
import com.moz1mozi.chat.message.repository.ChatMessageCustomRepository
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl

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
            .where(chatRoomMng.lastSeenDt.before(chatRoomMng.entryDt))
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

    override fun selectUnreadMessage(chatRoomId: Long, userId: Long): UnreadMessageResponse? {

        val unreadCountExpression = Expressions.numberPath(Long::class.java, "unreadCount")

        val result = queryFactory
            .select(
                chatRoomMng.chatUserPk.chatRoom.id,
                chatRoomMng.chatUserPk.user.id,
                chatMessage.count().`as`(unreadCountExpression)
            )
            .from(chatRoomMng)
            .join(chatMessage)
            .on(chatRoomMng.chatUserPk.chatRoom.id.eq(chatMessage.chatRoom.id))
            .where(chatRoomMng.chatUserPk.chatRoom.id.eq(chatRoomId))
            .where(chatRoomMng.chatUserPk.user.id.eq(userId))
            .where(chatRoomMng.entryDt.before(chatRoomMng.lastSeenDt))
            .where(chatMessage.msgDt.gt(chatRoomMng.lastSeenDt))
            .where(chatRoomMng.entryStat.eq(Status.ENABLED))
            .groupBy(chatRoomMng.chatUserPk.user.id)
            .fetchOne()

        return result?.let {
            UnreadMessageResponse(
                chatRoomId = it.get(chatRoomMng.chatUserPk.chatRoom.id)!!,
                userId = it.get(chatRoomMng.chatUserPk.user.id)!!,
                unreadCount = it.get(unreadCountExpression) ?: 0L
            )
        }
    }

    override fun selectMessage(
        chatRoomId: Long,
        pageable: Pageable
    ): Slice<ChatMessage> {
        val results = queryFactory
            .select(chatMessage)
            .from(chatMessage)
            .where(chatMessage.chatRoom.id.eq(chatRoomId))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong() + 1)
            .orderBy(chatMessage.msgDt.desc())
            .fetch()

        val hasNext = results.size > pageable.pageSize

        if (hasNext) results.removeAt(results.size - 1)

        return SliceImpl(results, pageable, hasNext)
    }
}