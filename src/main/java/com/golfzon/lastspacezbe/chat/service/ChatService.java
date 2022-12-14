package com.golfzon.lastspacezbe.chat.service;

import com.golfzon.lastspacezbe.chat.entity.ChatMessage;
import com.golfzon.lastspacezbe.chat.repository.ChatRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;

    public ChatMessage chattingHandler(ChatMessage chatting) {
        Member member = memberRepository.findById(chatting.getMember().getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자입니다."));

        chatting.setMember(member);

        return chatRepository.save(chatting);
    }
}
