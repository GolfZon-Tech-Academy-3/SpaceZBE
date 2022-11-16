package com.golfzon.lastspacezbe.member.dto;

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
