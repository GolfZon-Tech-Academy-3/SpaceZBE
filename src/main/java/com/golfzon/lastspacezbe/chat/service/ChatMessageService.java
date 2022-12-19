package com.golfzon.lastspacezbe.chat.service;

import com.golfzon.lastspacezbe.chat.dto.ChatMessageDto;
import com.golfzon.lastspacezbe.chat.entity.ChatMessage;
import com.golfzon.lastspacezbe.chat.pubsub.RedisPublisher;
import com.golfzon.lastspacezbe.chat.repository.ChatRepository;
import com.golfzon.lastspacezbe.chat.repository.ChatRoomRepository;
import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.security.jwt.JwtDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final RedisPublisher redisPublisher;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;

    private final JwtDecoder jwtDecoder;

    public void save(ChatMessageDto messageDto, String token) {
        log.info("save Message : {}", messageDto.getMessage());
        // username 세팅
        String sender = ""; // 업체명
        String userEmail = ""; // 업체명

        log.info("token : {}", token);

//        if(messageDto.getMessage().trim().equals("") && messageDto.getType()!= ChatMessage.MessageType.ENTER){
//            throw new CustomException(NO_MESSAGE);
//        }

        if (!(String.valueOf(token).equals("Authorization") || String.valueOf(token).equals("null"))) {
            String tokenInfo = token.substring(7); // Bearer빼고
            userEmail = jwtDecoder.decodeUsername(tokenInfo);
            log.info("user email : {}",userEmail);

            Optional<Member> member = memberRepository.findByUsername(userEmail);
            Company company = companyRepository.findByMember(member.get());
            log.info("company : {}", company);
            if (member.get().getAuthority().equals("master")) {
                sender = "master"; // 마스터
            }else if (member.get().getAuthority().equals("manager")){
                sender = company.getCompanyName(); // 업체명
            }
        }

        ChatMessage message = new ChatMessage(messageDto);

        message.setSender(sender);

        // 시간 세팅
        Date date = new Date();
        message.setCreatedAt(date);
        if (message.getType().equals("ENTER")) {
            chatRoomRepository.enterChatRoom(message.getRoomId());
            message.setMessage(message.getSender() + "님이 입장하셨습니다.");
        } else {
            chatMessageRepository.save(message);
        }
        // Websocket에 발행된 메시지를 redis로 발행한다(publish)
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }

    // redis 에 저장되어 있는 message 출력
    public List<ChatMessage> getMessages(String roomId) {
        log.info("getMessages roomId : {}", roomId);
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllMessage(roomId);

        return chatMessageList;
    }
}
