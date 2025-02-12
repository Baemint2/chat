package com.moz1mozi.chat.room.repository.impl

import com.moz1mozi.chat.entity.QChatRoom
import com.moz1mozi.chat.entity.QChatRoomMng
import com.moz1mozi.chat.entity.QUser
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
            "GROUP_CONCAT({0})", QUser.user.username
        )

        val subQuery = JPAExpressions
            .select(QChatRoomMng.chatRoomMng.chatUserPk.chatRoom.id)
            .from(QChatRoomMng.chatRoomMng)
            .join(QUser.user).on(QChatRoomMng.chatRoomMng.chatUserPk.user.id.eq(QUser.user.id))
            .where(QUser.user.username.eq(username), QChatRoomMng.chatRoomMng.entryStat.eq(Status.ENABLED))

        val results = queryFactory
            .select(
                QChatRoom.chatRoom.id,
                QChatRoom.chatRoom.chatRoomTitle,
                QChatRoom.chatRoom.creator,
                QChatRoom.chatRoom.createdAt,
                QChatRoom.chatRoom.updatedAt,
                groupConcat)
            .from(QChatRoom.chatRoom)
            .join(QChatRoomMng.chatRoomMng)
            .on(QChatRoom.chatRoom.id.eq(QChatRoomMng.chatRoomMng.chatUserPk.chatRoom.id))
            .join(QUser.user)
            .on(QChatRoomMng.chatRoomMng.chatUserPk.user.id.eq(QUser.user.id))
            .where(
                QChatRoomMng.chatRoomMng.entryStat.eq(Status.ENABLED)
                 , QChatRoom.chatRoom.chatRoomStat.eq(Status.ENABLED)
                 , QChatRoom.chatRoom.id.`in`(subQuery))
            .groupBy(QChatRoom.chatRoom.id)
            .fetch()
        return results.map { tuple ->
            val usernames = tuple.get(groupConcat)?.split(",") ?: emptyList()

            val userInfos = usernames.map { username ->
                val userEntity = queryFactory
                    .select(QUser.user)
                    .from(QUser.user)
                    .where(QUser.user.username.eq(username))
                    .fetchOne()

                UserInfo(
                    username = userEntity?.username ?: "Unknown",
                    nickname = userEntity?.nickname ?: "Unknown",
                )
            }

            ChatRoomSearchResponse(
                chatRoomId = tuple.get(QChatRoom.chatRoom.id)!!,
                chatRoomTitle = tuple.get(QChatRoom.chatRoom.chatRoomTitle),
                creator = tuple.get(QChatRoom.chatRoom.creator),
                createdAt = tuple.get(QChatRoom.chatRoom.createdAt)!!,
                updatedAt = tuple.get(QChatRoom.chatRoom.updatedAt),
                participantUsers = userInfos
            )
        }
    }
}