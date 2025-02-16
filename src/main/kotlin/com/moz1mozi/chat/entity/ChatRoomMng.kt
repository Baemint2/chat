package com.moz1mozi.chat.entity

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.time.LocalDateTime

@Entity
class ChatRoomMng(

    @EmbeddedId
    val chatUserPk: ChatUserPK,

    // 채팅방 입장상태
    @Enumerated(EnumType.STRING)
    var entryStat: Status? = Status.ENABLED,

    // 채팅방 알림상태
    @Enumerated(EnumType.STRING)
    var alarmStat: Status? = Status.ENABLED,

    // 채팅방 초대 or 생성시간
    var accessDt: LocalDateTime = LocalDateTime.now(),

    // 채팅방 접속시간
    var entryDt: LocalDateTime = LocalDateTime.now(),

    // 채팅방 마지막 접속시간
    var lastSeenDt: LocalDateTime? = null,

) {

    override fun toString(): String {
        return "ChatRoomMng(chatUserPk=$chatUserPk, entryStat=$entryStat, alarmStat=$alarmStat, accessDt=$accessDt, entryDt=$entryDt, lastSeenDt=$lastSeenDt)"
    }
}