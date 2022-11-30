package com.golfzon.lastspacezbe.company.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyJoinResponseDto {

    Long companyId; // 업체번호
    String companyName; // 업체명
    String location; //업체 위치
    String info; // 업체 정보
    String details; // 상세 정보
    String approveStatus; // 활동상태
    String imageName; // 업체 이미지

    String profileImage; // 프로필 이미지
    String memberName; // 회원 이름
    String email; // 이메일

}
