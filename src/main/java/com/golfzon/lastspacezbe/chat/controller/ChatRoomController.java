package com.golfzon.lastspacezbe.chat.controller;

import com.golfzon.lastspacezbe.chat.dto.ChatRoomDto;
import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import com.golfzon.lastspacezbe.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;

    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> room(@RequestHeader("Authorization") String token) {
        log.info("요청 메서드 [GET] /chat/rooms");
        return chatRoomRepository.findAllRoom(token);
    }

    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {

        log.info("요청 메서드 [GET] /chat/room/{roomId}");
        return chatRoomRepository.findRoomById(roomId);
    }
}
