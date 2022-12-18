package com.golfzon.lastspacezbe.member.entity;

import com.golfzon.lastspacezbe.chat.entity.ChatRoom;
import com.golfzon.lastspacezbe.member.dto.SignupRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member{

    @Id //pk설정
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_user")
    @SequenceGenerator(sequenceName = "seq_user", allocationSize = 1, name="seq_user")
    @Column
    private Long memberId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String memberName;

    @Column
    private String authority;

    @Column
    private String imgName;

    public Member(SignupRequestDto signupRequestDto) {
        this.username = signupRequestDto.getEmail();
        this.password = signupRequestDto.getPassword();
        this.memberName = signupRequestDto.getMemberName();
        this.authority = signupRequestDto.getAuthority();
        this.imgName = signupRequestDto.getImgName();
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", email='" + username + '\'' +
                ", password='" + password + '\'' +
                ", memberName='" + memberName + '\'' +
                ", authority='" + authority + '\'' +
                ", imgName='" + imgName + '\'' +
                '}';
    }
}
