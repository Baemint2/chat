package com.moz1mozi.chat.room.repository.impl

import com.moz1mozi.chat.entity.QChatMessage
import com.moz1mozi.chat.entity.QChatMessage.chatMessage
import com.moz1mozi.chat.entity.QChatRoom.chatRoom
import com.moz1mozi.chat.entity.QChatRoomMng.chatRoomMng
import com.moz1mozi.chat.entity.QUser
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
    val latelyMessage = QChatMessage("latelyMessage")
    val unreadCount = QChatMessage("unreadCount")
    val participants = QUser("participants")

    override fun selectChatRoom(username: String): List<ChatRoomSearchResponse> {
        val groupConcatUserInfo = Expressions.stringTemplate(
            "GROUP_CONCAT(CONCAT({0}, ':', {1}))", participants.username, participants.nickname
        )

        val userCount = Expressions.numberPath(Long::class.java, "userCount")


        val participants = JPAExpressions
            .select(groupConcatUserInfo)
            .from(participants)
            .where(
                participants.id.`in`(
                    JPAExpressions
                        .select(chatRoomMng.chatUserPk.user.id)
                        .from(chatRoomMng)
                        .where(chatRoomMng.chatUserPk.chatRoom.id.eq(chatRoom.id))
                        .where((JPAExpressions.select(chatRoomMng.count().`as`(userCount))
                            .from(chatRoomMng)
                            .where(chatRoomMng.chatUserPk.chatRoom.id.eq(chatRoom.id))).lt(3)
                        .or(chatRoomMng.entryStat.eq(Status.ENABLED)))
                )
            )

        val unreadCountExpression = Expressions.numberPath(Long::class.java, "unreadCount")

        val latelyMessage = JPAExpressions
            .select(latelyMessage.msgContent)
            .from(latelyMessage)
            .where(latelyMessage.chatRoom.id.eq(chatRoom.id),
                latelyMessage.msgDt.eq(
                    JPAExpressions
                        .select(chatMessage.msgDt.max())
                        .from(chatMessage)
                        .where(chatMessage.chatRoom.id.eq(chatRoom.id))
                ))
            .orderBy(latelyMessage.msgDt.desc())

        val findUserId = JPAExpressions
            .select(user.id)
            .from(user)
            .where(user.username.eq(username))

        val lastSeenDt = JPAExpressions
            .select(chatRoomMng.lastSeenDt)
            .from(chatRoomMng)
            .where(chatRoomMng.chatUserPk.user.id.eq(findUserId))
            .where(chatRoomMng.chatUserPk.chatRoom.id.eq(chatRoom.id))
            .where(chatRoomMng.entryStat.eq(Status.ENABLED))

        val unreadCount = JPAExpressions
            .select(unreadCount.count().`as`(unreadCountExpression))
            .from(unreadCount)
            .where(unreadCount.chatRoom.id.eq(chatRoom.id))
            .where(unreadCount.msgDt.gt(lastSeenDt))

        val results = queryFactory
            .select(
                chatRoom.id,
                chatRoom.chatRoomTitle,
                chatRoom.creator,
                chatRoom.createdAt,
                chatRoom.updatedAt,
                unreadCount,
                participants,
                latelyMessage)
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
                , chatRoomMng.chatUserPk.user.id.eq(findUserId)
                , chatMessage.msgContent.isNotNull)
            .groupBy(chatRoom.id)
            .orderBy(chatMessage.msgDt.max().desc())
            .fetch()
        return results.map { tuple ->
            val userInfos = tuple.get(participants)
                ?.split(",")
                ?.map { info ->
                    val (username, nickname) = info.split(":")
                    UserInfo(username, nickname)
                } ?: emptyList()

            ChatRoomSearchResponse(
                chatRoomId = tuple.get(chatRoom.id)!!,
                chatRoomTitle = tuple.get(chatRoom.chatRoomTitle),
                creator = tuple.get(chatRoom.creator),
                createdAt = tuple.get(chatRoom.createdAt)!!,
                updatedAt = tuple.get(chatRoom.updatedAt),
                participantUsers = userInfos,
                latelyMessage = tuple.get(latelyMessage),
                unreadCount = tuple.get(unreadCount)
            )
        }
    }
}