package com.moz1mozi.chat.entity

import jakarta.persistence.*

@Entity
class ChatRoom(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    var id: Long? = null,

    // 채팅방 제목
    var chatRoomTitle: String? = null,

    // 채팅방 상태,
    @Enumerated(EnumType.STRING)
    var chatRoomStat: Status? = Status.ENABLED,

    @OneToMany(mappedBy = "chatRoom")
    val chatMessage: MutableList<ChatMessage> = mutableListOf(),

    @OneToMany(mappedBy = "chatUserPk.chatRoom", cascade = [CascadeType.ALL], orphanRemoval = true)
    val chatRoomMng: MutableList<ChatRoomMng> = mutableListOf()

): BaseEntity() {
}