package com.golfzon.lastspacezbe.member.entity;

import com.golfzon.lastspacezbe.member.dto.SignupRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id //pk설정
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_member")
    @SequenceGenerator(sequenceName = "seq_member", allocationSize = 1, name="seq_member")
    @Column
    private long memberId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String memberName;

    @Column
    private String authority;

    @Column
    private String imgName;

    public Member(SignupRequestDto signupRequestDto) {
        this.email = signupRequestDto.getEmail();
        this.password = signupRequestDto.getPassword();
        this.memberName = signupRequestDto.getMemberName();
        this.authority = signupRequestDto.getAuthority();
        this.imgName = signupRequestDto.getImgName();
    }


//    @Column
//    private MultipartFile multipartFile;


}
