package com.golfzon.lastspacezbe.chat.entity;

import com.golfzon.lastspacezbe.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder // Builder 는 파라미터가 없는 기본생성자와는 쓸 수 없다.
@Entity(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_room")
    @SequenceGenerator(sequenceName = "seq_room", allocationSize = 1, name="seq_room")
    @Column(name = "id")
    private Long id;
    private String roomId;
    private String name;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    public static ChatRoom create(String name) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = UUID.randomUUID().toString();
        chatRoom.name = name;
        return chatRoom;
    }
}