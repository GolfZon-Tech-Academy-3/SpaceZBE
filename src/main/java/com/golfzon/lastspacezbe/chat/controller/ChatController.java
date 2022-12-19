package com.golfzon.lastspacezbe.chat.controller;

import com.golfzon.lastspacezbe.chat.dto.ChatMessageDto;
import com.golfzon.lastspacezbe.chat.entity.ChatMessage;
import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import com.golfzon.lastspacezbe.chat.repository.ChatRepository;
import com.golfzon.lastspacezbe.chat.repository.ChatRoomRepository;
import com.golfzon.lastspacezbe.chat.service.ChatMessageService;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import com.golfzon.lastspacezbe.security.jwt.JwtDecoder;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;

    private final JwtDecoder jwtDecoder;

    // pub/chat/message
    private final ChatMessageService chatMessageService;
    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessageDto message, @Header("Authorization") String token) {
        log.info("요청 메서드 [Message] /chat/message");
        chatMessageService.save(message, token);
    }

    @GetMapping("/chat/message/{roomId}")
    @ResponseBody
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        log.info("요청 메서드 [GET] /chat/message/{roomId}");
        return chatMessageService.getMessages(roomId);
    }
}
