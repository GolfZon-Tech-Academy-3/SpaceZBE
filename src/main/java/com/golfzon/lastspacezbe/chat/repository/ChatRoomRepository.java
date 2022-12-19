package com.golfzon.lastspacezbe.chat.repository;

import com.golfzon.lastspacezbe.chat.dto.ChatRoomDto;
import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import com.golfzon.lastspacezbe.chat.pubsub.RedisSubscriber;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import com.golfzon.lastspacezbe.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChatRoomRepository {
    // 채팅방(topic)에 발행되는 메시지를 처리할 Listner
    private final RedisMessageListenerContainer redisMessageListener;
    // 구독 처리 서비스
    private final RedisSubscriber redisSubscriber;
    // Redis
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    // 채팅방의 대화 메시지를 발행하기 위한 redis topic 정보. 서버별로 채팅방에 매치되는 topic정보를 Map에 넣어 roomId로 찾을수 있도록 한다.
    private Map<String, ChannelTopic> topics;

    private final MemberRepository memberRepository;
    private final ChatRepository chatMessageRepository;
    private final ChatRoomsRepository chatRoomsRepository;
    private final ChatRoomService chatRoomService;

    private final JwtDecoder jwtDecoder;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }

    public List<ChatRoomDto> findAllRoom(String token) {

        String tokenInfo = token.toString().substring(7); // Bearer빼고
        log.info("tokenInfo :{}", tokenInfo);
        String userEmail = jwtDecoder.decodeUsername(tokenInfo);
        log.info("user_email :{}", userEmail);

        Optional<Member> member = memberRepository.findByUsername(userEmail);
        List<ChatRoomDto> chatRoomDtoList = new ArrayList<>();
        List<ChatRoom> rooms = new ArrayList<>();

        if (member.get().getAuthority().equals("master")){
            rooms = chatRoomsRepository.findAllByOrderByIdDesc();
            for (ChatRoom chatRoom: rooms
                 ) {
                ChatRoomDto chatRoomDto = new ChatRoomDto();
                chatRoomDto.setRoomId(chatRoom.getRoomId());
                chatRoomDto.setName(chatRoom.getName());

                chatRoomDtoList.add(chatRoomDto);
            }
        } else if(member.get().getAuthority().equals("manager")) {
            rooms = chatRoomsRepository.findAll();
            for (ChatRoom chatRoom: rooms
            ) {
                log.info("room.getMember() :{}", chatRoom.getMember());
                // 저장되어있는 memberId 값이 같을때
                if(chatRoom.getMember().getMemberId().equals(member.get().getMemberId())){
                    ChatRoomDto chatRoomDto = new ChatRoomDto();
                    chatRoomDto.setRoomId(chatRoom.getRoomId());
                    chatRoomDto.setName(chatRoom.getName());

                    chatRoomDtoList.add(chatRoomDto);
                }
            }
        }
        return chatRoomDtoList;
    }
    ObjectMapper objectMapper = new ObjectMapper();
    // 채팅방 조회
    public ChatRoom findRoomById(String roomId) {
        return objectMapper.convertValue(opsHashChatRoom.get(CHAT_ROOMS, roomId),ChatRoom.class);
    }

    /**
     * 채팅방 생성 : 서버간 채팅방 공유를 위해 redis hash에 저장한다.
     */
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    /**
     * 채팅방 입장 : redis에 topic을 만들고 pub/sub 통신을 하기 위해 리스너를 설정한다.
     */
    public void enterChatRoom(String roomId) {
        log.info("enterChatRoom roomId : {}", roomId);
        ChannelTopic topic = topics.get(roomId);
        if (topic == null)
            topic = new ChannelTopic(roomId);
        redisMessageListener.addMessageListener(redisSubscriber, topic);
        topics.put(roomId, topic);
    }

    public ChannelTopic getTopic(String roomId) {
        log.info("getTopic");
        log.info("roomId : {}", roomId);
        return topics.get(roomId);
    }
}
