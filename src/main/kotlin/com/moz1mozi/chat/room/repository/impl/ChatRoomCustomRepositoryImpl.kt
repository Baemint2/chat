package com.moz1mozi.chat.room.repository.impl

import com.moz1mozi.chat.entity.QChatMessage.chatMessage
import com.moz1mozi.chat.entity.QChatRoom.chatRoom
import com.moz1mozi.chat.entity.QChatRoomMng.chatRoomMng
import com.moz1mozi.chat.entity.QUser.user
import com.moz1mozi.chat.entity.Status
import com.moz1mozi.chat.room.dto.ChatRoomSearchResponse
import com.moz1mozi.chat.room.repository.ChatRoomCustomRepository
import com.moz1mozi.chat.user.dto.UserInfo
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager

class ChatRoomCustomRepositoryImpl (
    entityManager: EntityManager
): ChatRoomCustomRepository {
    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    override fun selectChatRoom(username: String): List<ChatRoomSearchResponse> {
        val groupConcat = Expressions.stringTemplate(
            "GROUP_CONCAT(distinct {0})", user.username
        )

        val subQuery = JPAExpressions
            .select(chatRoomMng.chatUserPk.chatRoom.id)
            .from(chatRoomMng)
            .join(user).on(chatRoomMng.chatUserPk.user.id.eq(user.id))
            .where(user.username.eq(username), chatRoomMng.entryStat.eq(Status.ENABLED))

        val results = queryFactory
            .select(
                chatRoom.id,
                chatRoom.chatRoomTitle,
                chatRoom.creator,
                chatRoom.createdAt,
                chatRoom.updatedAt,
                Expressions.`as`(Expressions.constant(chatMessage.msgDt.max()), "latest_message_dt"),
                groupConcat)
            .from(chatRoom)
            .join(chatRoomMng)
            .on(chatRoom.id.eq(chatRoomMng.chatUserPk.chatRoom.id))
            .join(user)
            .on(chatRoomMng.chatUserPk.user.id.eq(user.id))
            .join(chatMessage)
            .on(chatRoom.id.eq(chatMessage.chatRoom.id))
            .where(
                chatRoomMng.entryStat.eq(Status.ENABLED)
                 , chatRoom.chatRoomStat.eq(Status.ENABLED)
                 , chatRoom.id.`in`(subQuery))
            .groupBy(chatRoom.id)
            .orderBy(chatMessage.msgDt.max().desc())
            .fetch()
        return results.map { tuple ->
            val usernames = tuple.get(groupConcat)?.split(",") ?: emptyList()

            val userInfos = usernames.map { username ->
                val userEntity = queryFactory
                    .select(user)
                    .from(user)
                    .where(user.username.eq(username))
                    .fetchOne()

                UserInfo(
                    username = userEntity?.username ?: "Unknown",
                    nickname = userEntity?.nickname ?: "Unknown",
                )
            }

            ChatRoomSearchResponse(
                chatRoomId = tuple.get(chatRoom.id)!!,
                chatRoomTitle = tuple.get(chatRoom.chatRoomTitle),
                creator = tuple.get(chatRoom.creator),
                createdAt = tuple.get(chatRoom.createdAt)!!,
                updatedAt = tuple.get(chatRoom.updatedAt),
                participantUsers = userInfos,
            )
        }
    }
}