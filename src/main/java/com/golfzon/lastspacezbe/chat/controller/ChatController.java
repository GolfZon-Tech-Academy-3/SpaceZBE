package com.golfzon.lastspacezbe.chat.controller;

import com.golfzon.lastspacezbe.chat.dto.ChatMessageDto;
import com.golfzon.lastspacezbe.chat.entity.ChatMessage;
import com.golfzon.lastspacezbe.chat.repository.ChatRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRepository chatRepository;

    // pub/chat/message
    @MessageMapping("/chat/message")
    public ResponseEntity<ChatMessageDto> message(@RequestBody ChatMessageDto message) throws Exception{

        ChatMessage chatMessage = new ChatMessage();

        System.out.println(message);

        if (message.getType().equals("ENTER")){
            chatMessage.setMessage(message.getSender() + "님이 입장하셨습니다.");
            chatMessage.setSender(message.getSender());
            chatMessage.setType(message.getType());
            chatMessage.setRoomId(message.getRoomId());

            chatRepository.save(chatMessage);
        }else if (message.getType().equals("TALK")){
            chatMessage.setMessage(message.getMessage());
            chatMessage.setSender(message.getSender());
            chatMessage.setType(message.getType());
            chatMessage.setRoomId(message.getRoomId());

            chatRepository.save(chatMessage);
        }

        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        return ResponseEntity.ok()
                .body(message);
    }
}
