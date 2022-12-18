package com.golfzon.lastspacezbe.chat.controller;

import com.golfzon.lastspacezbe.chat.dto.ChatMessageDto;
import com.golfzon.lastspacezbe.chat.entity.ChatMessage;
import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import com.golfzon.lastspacezbe.chat.repository.ChatRepository;
import com.golfzon.lastspacezbe.chat.repository.ChatRoomRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
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
    @MessageMapping("/chat/message")
    public ResponseEntity<ChatMessageDto> message(@RequestBody ChatMessageDto message, @Header("Authorization") String token) throws Exception{

        log.info("token :{}", token);
        String tokenInfo = token.substring(7); // Bearer빼고
        log.info("tokenInfo :{}", tokenInfo);
        String userEmail = jwtDecoder.decodeUsername(tokenInfo);
        log.info("user_email :{}", userEmail);

        Optional<Member> member = memberRepository.findByUsername(userEmail);

        ChatMessage chatMessage = new ChatMessage();

        System.out.println(message);
        if(member.get().getAuthority().equals("master")){
            if (message.getType().equals("ENTER")) {
                chatMessage.setMessage(message.getSender() + "님이 입장하셨습니다.");
                chatMessage.setSender(message.getSender());
                chatMessage.setType(message.getType());
                chatMessage.setRoomId(message.getRoomId());

                chatRepository.save(chatMessage);
            } else if (message.getType().equals("TALK")) {
                chatMessage.setMessage(message.getMessage());
                chatMessage.setSender(message.getSender());
                chatMessage.setType(message.getType());
                chatMessage.setRoomId(message.getRoomId());

                chatRepository.save(chatMessage);
            }

            messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        }else if(member.get().getAuthority().equals("manager")){
            if (message.getType().equals("ENTER")) {
                chatMessage.setMessage(message.getSender() + "님이 입장하셨습니다.");
                chatMessage.setSender(message.getSender());
                chatMessage.setType(message.getType());
                chatMessage.setRoomId(message.getRoomId());

                chatRepository.save(chatMessage);
            } else if (message.getType().equals("TALK")) {
                chatMessage.setMessage(message.getMessage());
                chatMessage.setSender(message.getSender());
                chatMessage.setType(message.getType());
                chatMessage.setRoomId(message.getRoomId());

                chatRepository.save(chatMessage);
            }

            ChatRoom chatRoom = chatRoomRepository.findByMember(member.get());
            log.info("roomId :{}", chatRoom.getRoomId());
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getRoomId(), message);
        }

        return ResponseEntity.ok()
                .body(message);
    }
}
