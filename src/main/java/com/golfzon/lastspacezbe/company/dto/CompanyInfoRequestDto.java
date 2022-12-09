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
public class CompanyInfoRequestDto {

    Long companyId; // 업체 번호
    String companyName; // 업체명
    String location; // 업체 위치
    String details; // 상세 주소
    String info; // 업체 장소 소개
    String summary; // 소개 요약
    String rules; // 이용 규칙
    String imageName; // 업체 사진
    MultipartFile multipartFile;
}
