package com.golfzon.lastspacezbe.space.service;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.company.repository.CompanyRepository;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.space.dto.SpaceBackOfficeResponseDto;
import com.golfzon.lastspacezbe.space.dto.SpaceRequestDto;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.entity.SpaceImage;
import com.golfzon.lastspacezbe.space.repository.SpaceImageRepository;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final CompanyRepository companyRepository;
    private final SpaceImageRepository spaceImageRepository;
    private final SpaceS3Service spaceS3Service;


    public void spaceRegister(SpaceRequestDto requestDto){

//        Member member1 = memberRepository.findById(member.getMemberId()).orElseThrow(
//                () -> new IllegalArgumentException("접근할 수 없는 유저 입니다.")
//        );
//        log.info("member1 : {}",member1);
//
//        Company company = memberRepository.findByMemberId(member1.getMemberId()).orElseThrow(
//                () -> new IllegalArgumentException("접근할 수 없는 업체 입니다..")
//        );
//        log.info("company : {}",company);

        List<String> imagePaths = new ArrayList<>();
        if(requestDto.getFiles() == null){
            imagePaths.add("");
        } else {
            imagePaths.addAll(spaceS3Service.upload(requestDto.getFiles()));
        }

        Space space = new Space(requestDto.getSpaceName(),requestDto.getFacilities(),requestDto.getType(),
                requestDto.getPrice(),requestDto.getOpenTime(), requestDto.getCloseTime(),
                requestDto.getBreakOpen(), requestDto.getBreakClose(),requestDto.getCompanyId());

        // 등록 정보 저장
        spaceRepository.save(space);

        log.info("spaceId : {}", space.getSpaceId());
        for (String imagePath : imagePaths){
            SpaceImage spaceImage = new SpaceImage(imagePath, space);
            spaceImageRepository.save(spaceImage);
        }
    }

    public void spaceUpdate(Long spaceId, SpaceRequestDto requestDto) {

        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 spaceId는 존재하지 않습니다."));
        space.setSpaceName(requestDto.getSpaceName());
        space.setPrice(requestDto.getPrice());
        space.setFacilities(requestDto.getFacilities());
        space.setType(requestDto.getType());
        space.setOpenTime(requestDto.getOpenTime());
        space.setCloseTime(requestDto.getCloseTime());
        space.setBreakOpen(requestDto.getBreakOpen());
        space.setBreakClose(requestDto.getBreakClose());

        spaceRepository.save(space);
    }

    // 백오피스 사무공간 조회
    public List<SpaceBackOfficeResponseDto> spaceSelectAll(Long companyId) {

        List<SpaceBackOfficeResponseDto> responseDtos = new ArrayList<>();

        List<Space> spaces = spaceRepository.findAllByCompanyId(companyId);
        for (Space data: spaces
             ) {
            SpaceBackOfficeResponseDto responseDto = new SpaceBackOfficeResponseDto();
            responseDto.setSpaceName(data.getSpaceName());
            responseDto.setSpaceId(data.getSpaceId());
            responseDto.setType(data.getType());
            responseDto.setPrice(data.getPrice()); // 가격
            responseDto.setOpenTime(data.getOpenTime());
            responseDto.setCloseTime(data.getCloseTime());
            responseDto.setBreakOpen(data.getBreakOpen());
            responseDto.setBreakClose(data.getBreakClose());
            responseDto.setFacilities(data.getFacilities());

            responseDtos.add(responseDto);
        }

        return responseDtos;
    }

    // 사무공간 삭제
    public void spaceDelete(Long spaceId) {
        spaceRepository.deleteById(spaceId);
    }
}
