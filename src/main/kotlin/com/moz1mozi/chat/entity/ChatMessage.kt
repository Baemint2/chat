package com.moz1mozi.chat.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class ChatMessage(
    // 채팅메시지 내용
    var msgContent: String? = null,

    // 채팅메시지 전송시간
    var msgDt: LocalDateTime = LocalDateTime.now(),

    // 메시지 상태
    @Enumerated(EnumType.STRING)
    var msgStat: Status? = Status.ENABLED,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    val chatRoom: ChatRoom,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
): BaseEntity() {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msg_id")
    val id: Long? = null
}