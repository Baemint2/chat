package com.moz1mozi.chat.entity

import jakarta.persistence.*

@Entity
class ChatRoom(
    // 채팅방 제목
    var chatRoomTitle: String? = null,

    // 채팅방 상태,
    @Enumerated(EnumType.STRING)
    var chatRoomStat: Status? = Status.ENABLED,

    @OneToMany(mappedBy = "chatRoom")
    val chatMessage: MutableList<ChatMessage> = mutableListOf(),
): BaseEntity() {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    val id: Long? = null
}