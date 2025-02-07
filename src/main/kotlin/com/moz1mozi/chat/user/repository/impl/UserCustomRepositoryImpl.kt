package com.moz1mozi.chat.user.repository.impl

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
}