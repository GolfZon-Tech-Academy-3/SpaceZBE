package com.golfzon.lastspacezbe.inquiry.controller;


import com.golfzon.lastspacezbe.inquiry.dto.InquiryRequestDto;
import com.golfzon.lastspacezbe.inquiry.dto.InquiryResponseDto;
import com.golfzon.lastspacezbe.inquiry.service.InquiryBackOfficeService;
import com.golfzon.lastspacezbe.inquiry.service.InquiryService;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/back-office")
public class InquiryBackOfficeController {

    private final InquiryBackOfficeService inquiryBackOfficeService;

    // 백오피스 문의 내역
    @GetMapping(value = "/inquiry/total/{companyId}")
    public ResponseEntity<List<InquiryResponseDto>> inquirySelectAll(@PathVariable(name="companyId") Long companyId){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();
        log.info("member?:{}",member);

        List<InquiryResponseDto> responseDtos = inquiryBackOfficeService.inquirySelectAll(companyId);

        return ResponseEntity.ok()
                .body(responseDtos);
    }

}
