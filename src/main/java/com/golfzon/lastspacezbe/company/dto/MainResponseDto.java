package com.golfzon.lastspacezbe.company.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainResponseDto {

    Long companyId; // 업체 번호
    String companyName; // 업체명
    String location; //업체 위치
    String address; //업체 주소
    String details; //상세주소
    Set<String> types; // 등록된 type들
    Boolean companyLike; // 관심등록 여부(true, false)
    int lowPrice; // 최저가격
    String firstImage; // 대표이미지
    int reviewSize; // 리뷰개수
    double avgReview; // 총 리뷰 평균
}
