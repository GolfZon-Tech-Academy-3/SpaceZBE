package com.golfzon.lastspacezbe.chat.repository;

import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

// import 생략....

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom,Long> {

//    List<ChatRoom> findAll();
//    private Map<String, ChatRoom> chatRoomMap;
//
//    @PostConstruct
//    private void init() {
//        chatRoomMap = new LinkedHashMap<>();
//    }
//
//    public List<ChatRoom> findAllRoom() {
//        // 채팅방 생성순서 최근 순으로 반환
//        List chatRooms = new ArrayList<>(chatRoomMap.values());
//        Collections.reverse(chatRooms);
//        return chatRooms;
//    }
//
//    public ChatRoom findRoomById(String id) {
//        return chatRoomMap.get(id);
//    }
//
//    public ChatRoom createChatRoom(String name) {
//        ChatRoom chatRoom = ChatRoom.create(name);
//        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
//        return chatRoom;
//    }
}
