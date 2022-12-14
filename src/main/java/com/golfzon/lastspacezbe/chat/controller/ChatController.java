package com.golfzon.lastspacezbe.chat.controller;

import com.golfzon.lastspacezbe.chat.dto.ChatMessageDto;
import com.golfzon.lastspacezbe.chat.entity.ChatMessage;
import com.golfzon.lastspacezbe.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @Autowired
    private ChatService chatService;

    @MessageMapping("/receive")
    @SendTo("/send")
    public ChatMessage chattingHandler(ChatMessage chatting) {
        return chatService.chattingHandler(chatting);
    }
}
