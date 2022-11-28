package com.golfzon.lastspacezbe.company.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRequestDto {

    String companyName; // 업체명
    String info; // 업체 장소 소개
    String rules; // 이용 규칙
    String location; //업체 위치 (도로명 주소)
    String details; // 상세 주소
    String summary; // 소개 요약
    String approveStatus; //승인 상태

    String imageName; // 이미지 이름
    private MultipartFile multipartFile;
}
