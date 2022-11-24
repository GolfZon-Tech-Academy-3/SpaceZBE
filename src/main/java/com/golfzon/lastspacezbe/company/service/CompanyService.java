package com.golfzon.lastspacezbe.company.service;

import com.golfzon.lastspacezbe.company.Dto.CompanyResponseDto;
import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final SpaceRepository spaceRepository;
    //private final SpaceImageRepository spaceImageRepository;

    public CompanyResponseDto getCompanyInfo(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당하는 companyId가 없습니다."));

//        String companyName; // 업체명
//        String info; // 업체 장소 소개
//        String rules; // 이용 규칙
//        String location; //업체 위치
//        String summary; // 소개 요약

//        List<String> spaceImages; //공간 사진들 spaceImageRepository
//        List<Space> spaces; //등록된 공간들 spaceRepository
        List<String> spaceImages = new ArrayList<>();
        List<Space> spaces = spaceRepository.findAllByCompanyId(companyId);
//        for (Space space:spaces) {
//            SpaceImage spaceImage = spaceImageRepository.findBySpaceId(space.getSpaceId());
//            spaceImages.add(spaceImage.getImageName());
//        }

        return new CompanyResponseDto(company, spaceImages, spaces);
    }
}
