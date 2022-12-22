package com.golfzon.lastspacezbe.chat.entity;

import com.golfzon.lastspacezbe.chat.dto.ChatMessageDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ChatMessage {

    private String type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지

    private String createdAt; // 날짜

    public ChatMessage(ChatMessageDto dto){
        this.type = dto.getType();
        this.roomId = dto.getRoomId();
        this.message = dto.getMessage();
    }
}
