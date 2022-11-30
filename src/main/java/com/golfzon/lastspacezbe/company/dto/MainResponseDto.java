package com.golfzon.lastspacezbe.company.dto;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.space.entity.Space;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MainResponseDto {

    Long companyId; // 업체 번호
    String companyName; // 업체명
    String location; //업체 위치
    String details; //상세주소
    Set<String> types; // 등록된 type들
    Boolean companyLike; // 관심등록 여부(true, false)
    int lowPrice; // 최저가격
    String firstImage; // 대표이미지
    int reviewSize; // 리뷰개수
    double avgReview; // 총 리뷰 평균

   public MainResponseDto(Company company, List<String> spaceImages, List<Space> spaces, Boolean companyLike) {
        this.companyId = company.getCompanyId();
        this.companyName = company.getCompanyName();
        this.details = company.getDetails();
        this.companyLike = companyLike;
    }
}
