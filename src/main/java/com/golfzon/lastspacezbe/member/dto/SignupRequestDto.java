package com.golfzon.lastspacezbe.member.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDto {

    private long member_id;
    private String email;
    private String password;
    private String member_name;
    private String authority;
    private String img_name;
    private MultipartFile multipartFile;

}
