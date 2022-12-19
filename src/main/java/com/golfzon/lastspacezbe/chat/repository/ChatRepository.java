package com.golfzon.lastspacezbe.chat.repository;

import com.golfzon.lastspacezbe.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ChatRepository {
    // Redis
    private static final String CHAT_MESSAGE = "CHAT_MESSAGE";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, List<ChatMessage>> opsHashChatMessage;

    @PostConstruct
    private void init() {
        opsHashChatMessage = redisTemplate.opsForHash();
    }

    // 메세지 저장
    public ChatMessage save(ChatMessage chatMessage) {
        log.info("save");
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(List.class));

        String roomId = chatMessage.getRoomId();
        List<ChatMessage> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);
        if (chatMessageList == null) {
            chatMessageList = new ArrayList<>();
        }
        log.info("메세지를 저장하자!!!");
        chatMessageList.add(chatMessage);
        log.info("chatMessageList : {}", chatMessageList);

//        if (chatMessageList.size() > 10) {
//            log.info("chatMessageList size : {}", chatMessageList.size());
//            customEventPublisher.publish(roomId);
//            try {
//                // 메시지 리스트 txt 파일로 저장
//                List<ChatFile> foundList = chatFileRepository.findAllByRoomId(roomId);
//                int cnt;
//                ChatFile lastChatFile;
//                Long prevId = 0L;
//                if (foundList.size() == 0) {
//                    cnt = 0;
//                    lastChatFile = null;
//                } else {
//                    cnt = foundList.size();
//                    lastChatFile = foundList.get(cnt - 1);
//                    prevId = lastChatFile.getFileId();
//                }
//
//                log.info("cnt : {}", cnt);
//
//                String[] detail = chatFileService.fileWriter(chatMessageList.subList(0, 5), roomId);
//                String path = detail[0];
//                String fileName = detail[1];
//
//                File file = new File(path + fileName);
//
//                log.info("fileName : {}", file.getName());
//                //S3 에 txt 파일 업로드
//                //fileName = roomId _ cnt
//                String s3FileName = s3Validator.uploadTxtFile(file, cnt);
//
//                // 파일 db 에 저장
//                ChatFile chatFile = ChatFile.builder()
//                        .roomId(roomId)
//                        .fileUrl(s3FileName)
//                        .prevId(prevId)
//                        .build();
//                chatFileRepository.save(chatFile);
//
//                chatMessageList = deleteMessage(chatMessageList);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        opsHashChatMessage.put(CHAT_MESSAGE, roomId, chatMessageList);

        return chatMessage;
    }

    public List<ChatMessage> findAllMessage(String roomId) {
        log.info("findAllMessage");
        List<ChatMessage> chatMessageList = opsHashChatMessage.get(CHAT_MESSAGE, roomId);
        log.info("chatMessageList2 : {}", chatMessageList);
        return chatMessageList;
    }

    public Object findLastMessage(String roomId) {
        log.info("findLastMessage");
        if (opsHashChatMessage.get(CHAT_MESSAGE, roomId) == null || opsHashChatMessage.get(CHAT_MESSAGE, roomId).size() == 0) return null;
        return opsHashChatMessage.get(CHAT_MESSAGE, roomId).get(opsHashChatMessage.get(CHAT_MESSAGE, roomId).size() - 1);
    }

    public List<ChatMessage> deleteMessage(List<ChatMessage> chatMessageList) {
        log.info("deleteMessage");
        log.info("chatMessageList size : {}", chatMessageList.size());
        List<ChatMessage> newList = chatMessageList.subList(5, chatMessageList.size());
//        opsHashChatMessage.put(CHAT_MESSAGE, roomId, newList);
        return newList;
    }
}
