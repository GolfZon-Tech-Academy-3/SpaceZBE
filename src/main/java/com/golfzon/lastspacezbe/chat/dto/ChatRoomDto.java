package com.golfzon.lastspacezbe.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDto {

    public String roomId;
    public String name;
    public Object lastMessage;
}
