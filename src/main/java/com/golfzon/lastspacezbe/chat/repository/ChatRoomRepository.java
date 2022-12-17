package com.golfzon.lastspacezbe.chat.repository;

import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 채팅방 순서
    List<ChatRoom> findAllByOrderByIdDesc();
}
