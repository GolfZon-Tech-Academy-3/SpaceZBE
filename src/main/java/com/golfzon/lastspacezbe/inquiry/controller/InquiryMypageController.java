package com.golfzon.lastspacezbe.inquiry.controller;


import com.golfzon.lastspacezbe.inquiry.dto.InquiryResponseDto;
import com.golfzon.lastspacezbe.inquiry.service.InquiryMypageService;
import com.golfzon.lastspacezbe.inquiry.service.InquiryService;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class InquiryMypageController {

    private final InquiryMypageService inquiryMypageService;

    // 마이페이지 문의 내역
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
