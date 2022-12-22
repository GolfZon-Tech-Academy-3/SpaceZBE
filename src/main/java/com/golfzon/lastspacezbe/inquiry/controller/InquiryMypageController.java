package com.golfzon.lastspacezbe.inquiry.controller;


import com.golfzon.lastspacezbe.inquiry.dto.InquiryResponseDto;
import com.golfzon.lastspacezbe.inquiry.service.InquiryMypageService;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api(tags = "마이페이지 문의 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class InquiryMypageController {

    private final InquiryMypageService inquiryMypageService;

    // 마이페이지 문의 내역
    @ApiOperation(value = "마이페이지 문의내역", notes = "마이페이지 문의내역 조회 기능입니다.")
    @GetMapping(value = "/inquiry/total")
    public ResponseEntity<List<InquiryResponseDto>> inquirySelectAll(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();
        log.info("member?:{}",member);

        List<InquiryResponseDto> responseDtos = inquiryMypageService.inquirySelectAll(member);

        return ResponseEntity.ok()
                .body(responseDtos);
    }

}
