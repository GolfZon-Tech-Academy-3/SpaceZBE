package com.golfzon.lastspacezbe.inquiry.controller;


import com.golfzon.lastspacezbe.inquiry.dto.InquiryResponseDto;
import com.golfzon.lastspacezbe.inquiry.service.InquiryBackOfficeService;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping("/back-office")
public class InquiryBackOfficeController {

    private final InquiryBackOfficeService inquiryBackOfficeService;

    // 백오피스 문의 내역
    @ApiOperation(value = "백오피스 문의내역", notes = "백오피스 문의내역 조회 기능입니다.")
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
