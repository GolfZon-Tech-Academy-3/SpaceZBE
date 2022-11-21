package com.golfzon.lastspacezbe.space.service;

import com.golfzon.lastspacezbe.company.entity.Company;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.member.repository.MemberRepository;
import com.golfzon.lastspacezbe.space.dto.SpaceRequestDto;
import com.golfzon.lastspacezbe.space.entity.Space;
import com.golfzon.lastspacezbe.space.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final MemberRepository memberRepository;

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

        Space space = new Space(requestDto.getSpaceName(),requestDto.getFacilities(),requestDto.getType(),
                requestDto.getPrice(),requestDto.getOpenTime(), requestDto.getCloseTime(), requestDto.getBreakOpen(), requestDto.getBreakClose());
        // 등록 정보 저장
        spaceRepository.save(space);
    }

    public void spaceUpdate(Long spaceId, SpaceRequestDto requestDto) {

        Space space = spaceRepository.findAllBySpaceId(spaceId);
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
}