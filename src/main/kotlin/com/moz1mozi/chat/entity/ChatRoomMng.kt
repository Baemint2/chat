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

    // 채팅방 접속시간
    var entryDt: LocalDateTime = LocalDateTime.now(),

    // 채팅방 퇴장시간
    var exitDt: LocalDateTime? = null,

) {
}