package com.moz1mozi.chat.user.repository.impl

import com.moz1mozi.chat.entity.QChatRoomMng.chatRoomMng
import com.moz1mozi.chat.entity.QUser.user
import com.moz1mozi.chat.entity.User
import com.moz1mozi.chat.user.repository.UserCustomRepository
import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager

class UserCustomRepositoryImpl(
    entityManager: EntityManager,
): UserCustomRepository {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(entityManager)

    override fun searchUsers(searchText: String): List<User> {
        return queryFactory
            .select(user)
            .from(user)
            .where(user.nickname.like("%$searchText%"))
            .fetch()
    }

    override fun selectUsersNotInChatRoom(chatRoomId: Long): List<User> {
        return queryFactory.select(user)
            .from(user)
            .leftJoin(chatRoomMng)
            .on(user.id.eq(chatRoomMng.chatUserPk.user.id))
            .on(chatRoomMng.chatUserPk.chatRoom.id.eq(chatRoomId))
            .where(chatRoomMng.chatUserPk.user.id.isNull)
            .fetch()
    }
}