package com.golfzon.lastspacezbe.chat.repository;

import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomsRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByOrderByIdDesc();
}
