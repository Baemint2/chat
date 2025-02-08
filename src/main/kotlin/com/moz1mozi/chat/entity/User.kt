package com.moz1mozi.chat.entity

import jakarta.persistence.*
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null,
    @Column(unique = true) var username: String,
    var password: String,
    var nickname: String? = null,
) {
    @OneToMany(mappedBy = "user")
    val chatMessage: MutableList<ChatMessage> = mutableListOf()

    @Column(updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
}