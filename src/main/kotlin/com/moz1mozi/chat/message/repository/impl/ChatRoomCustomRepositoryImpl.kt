package com.moz1mozi.chat.message.repository.impl

import com.moz1mozi.chat.entity.ChatRoom
import com.moz1mozi.chat.entity.QChatRoom.chatRoom
import com.moz1mozi.chat.entity.QChatRoomMng.chatRoomMng
import com.moz1mozi.chat.entity.QUser
import com.moz1mozi.chat.entity.QUser.user
import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.message.dto.ChatRoomResponse
import com.moz1mozi.chat.message.repository.ChatRoomCustomRepository
import com.querydsl.core.Tuple
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions

class ChatRoomCustomRepositoryImpl (
    entityManager: EntityManager
): ChatRoomCustomRepository {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)
    override fun selectChatRoom(username: String): List<ChatRoomResponse> {
        val groupConcat = Expressions.stringTemplate(
            "GROUP_CONCAT({0} ORDER BY {0} SEPARATOR ', ')", user.username
        )

        val subQuery = JPAExpressions
            .select(chatRoomMng.chatUserPk.chatRoom.id)
            .from(chatRoomMng)
            .join(user).on(chatRoomMng.chatUserPk.user.id.eq(user.id))
            .where(user.username.eq(username), chatRoomMng.entryStat.eq(Status.ENABLED))

        val results = queryFactory
            .select(chatRoom.id,
                chatRoom.chatRoomTitle,
                chatRoom.creator,
                chatRoom.createdAt,
                chatRoom.updatedAt,
                groupConcat)
            .from(chatRoom)
            .join(chatRoomMng)
            .on(chatRoom.id.eq(chatRoomMng.chatUserPk.chatRoom.id))
            .join(user)
            .where(chatRoomMng.entryStat.eq(Status.ENABLED)
                 , chatRoom.chatRoomStat.eq(Status.ENABLED)
                 , chatRoom.id.`in`(subQuery))
            .groupBy(chatRoom.id)
            .fetch()
        return results.map { tuple ->
            ChatRoomResponse(
                chatRoomId = tuple.get(chatRoom.id)!!,
                chatRoomTitle = tuple.get(chatRoom.chatRoomTitle)!!,
                creator = tuple.get(chatRoom.creator)!!,
                createdAt = tuple.get(chatRoom.createdAt)!!,
                updatedAt = tuple.get(chatRoom.updatedAt)!!,
                participantUsers = tuple.get(groupConcat)!!
            )
        }
    }
}