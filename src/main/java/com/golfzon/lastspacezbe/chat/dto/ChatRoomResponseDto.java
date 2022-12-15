package com.golfzon.lastspacezbe.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponseDto {

    private String roomId; // 방번호
    private String name; // 방이름
}
