package com.golfzon.lastspacezbe.chat.entity;

import com.golfzon.lastspacezbe.member.entity.Member;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chat")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "message")
    private String message;

    @CreationTimestamp
    LocalDateTime reserveTime;

    @Column(name = "is_request")
    private Boolean isRequest;
}
