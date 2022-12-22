package com.golfzon.lastspacezbe.chat.service;

import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import com.golfzon.lastspacezbe.chat.repository.ChatRoomsRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomsRepository chatRoomsRepository;
    private final MemberRepository memberRepository;
    private final JwtDecoder jwtDecoder;
    private Map<String, ChatRoom> chatRoomMap;

    @PostConstruct
    private void init() {
        chatRoomMap = new LinkedHashMap<>();
    }

//    public List<ChatRoom> findAllRoom(String token) {
//        // 채팅방 생성순서 최근 순으로 반환
////        List chatRooms = new ArrayList<>(chatRoomMap.values());
//        String tokenInfo = token.toString().substring(7); // Bearer빼고
//        log.info("tokenInfo :{}", tokenInfo);
//        String userEmail = jwtDecoder.decodeUsername(tokenInfo);
//        log.info("user_email :{}", userEmail);
//
//        Optional<Member> member = memberRepository.findByUsername(userEmail);
//        List<ChatRoom> rooms = new ArrayList<>();
//
//        if (member.get().getAuthority().equals("master")){
//            rooms = chatRoomsRepository.findAllByOrderByIdDesc();
//
//        }else if(member.get().getAuthority().equals("manager")){
//            List<ChatRoom> chatRooms = chatRoomsRepository.findAll();
//            for (ChatRoom room: chatRooms
//                 ) {
//                log.info("room.getMember() :{}", room.getMember());
//                // 저장되어있는 memberId 값이 같을때
//                if(room.getMember().getMemberId().equals(member.get().getMemberId())){
//                    rooms.add(room);
//                }
//            }
//        }
//
//        // id , name , roomId
//        return rooms;
//    }

    public ChatRoom findRoomById(String id) {
        return chatRoomMap.get(id);
    }

    // DB 테이블 생성
    public ChatRoom createChatRoom(ChatRoom chatRoom) {
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);

        chatRoomsRepository.save(chatRoom);
        return chatRoom;
    }
}
