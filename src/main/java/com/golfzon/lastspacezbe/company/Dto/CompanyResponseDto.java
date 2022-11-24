package com.golfzon.lastspacezbe.company.Dto;

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
public class CompanyResponseDto {

    Long companyId; // 업체 번호
    String companyName; // 업체명
    String location; //업체 위치
    String summary; // 소개 요약
    List<String> spaceImages; //공간 사진들
    List<Space> spaces; //등록된 공간들
    String info; // 업체 장소 소개
    String rules; // 이용 규칙
    Boolean companyLike; // 관심등록 여부(true, false)

   public CompanyResponseDto(Company company, List<String> spaceImages, List<Space> spaces, Boolean companyLike) {
        this.companyId = company.getCompanyId();
        this.companyName = company.getCompanyName();
        this.location = company.getLocation();
        this.summary = company.getSummary();
        this.spaceImages = spaceImages;
        this.spaces = spaces;
        this.info = company.getInfo();
        this.rules = company.getRules();
        this.companyLike = companyLike;
    }
}
