package com.golfzon.lastspacezbe.member.dto;

import com.golfzon.lastspacezbe.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    private long memberId;
    private String email;
    private String password;
    private String memberName;
    private String authority;
    private String imgName;
    private MultipartFile multipartFile;

    public SignupRequestDto(Member member) {
        this.memberId = member.getMemberId();
        this.email = member.getUsername();
        this.memberName = member.getMemberName();
        this.authority = member.getAuthority();
        this.imgName = member.getImgName();
    }


    @Override
    public String toString() {
        return "SignupRequestDto{" +
                "memberId=" + memberId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", memberName='" + memberName + '\'' +
                ", authority='" + authority + '\'' +
                ", imgName='" + imgName + '\'' +
                ", multipartFile=" + multipartFile +
                '}';
    }
}
