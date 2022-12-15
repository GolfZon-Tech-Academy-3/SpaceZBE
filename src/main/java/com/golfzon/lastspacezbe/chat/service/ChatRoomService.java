package com.golfzon.lastspacezbe.chat.service;

import com.golfzon.lastspacezbe.chat.dto.ChatRoomResponseDto;
import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import com.golfzon.lastspacezbe.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatRoomService {

    // 채팅방 목록
    private final ChatRoomRepository chatRoomRepository;

    public List<ChatRoomResponseDto> findAllRooms(){

        List<ChatRoomResponseDto> responseDtos = new ArrayList<>();
        List<ChatRoom> rooms = chatRoomRepository.findAll();

        for (ChatRoom room: rooms
             ) {
            ChatRoomResponseDto responseDto = new ChatRoomResponseDto();
            responseDto.setRoomId(room.getRoomId());
            responseDto.setName(room.getName());

            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    // 채팅방 생성
    public void createChatRoom(String name) {

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);

        chatRoomRepository.save(chatRoom);
    }
}
