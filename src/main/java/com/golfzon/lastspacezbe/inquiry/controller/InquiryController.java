package com.golfzon.lastspacezbe.inquiry.controller;


import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.inquiry.dto.InquiryRequestDto;
import com.golfzon.lastspacezbe.inquiry.service.InquiryService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    // 문의하기
    @PostMapping(value = "/post/{spaceId}", produces="text/plain;charset=UTF-8")
    public ResponseEntity<String> inquiry(@PathVariable(name="spaceId") Long spaceId,
                                           @RequestBody InquiryRequestDto requestDto) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();
        log.info("member?:{}",member);

        inquiryService.inquiry(spaceId,requestDto,member);

        return ResponseEntity.ok()
                .body("result : 문의내용 작성완료");
    }

    // 문의 내용 삭제
    @DeleteMapping(value = "/delete/{inquiryId}", produces="text/plain;charset=UTF-8")
    public ResponseEntity<String> inquiryDelete(@PathVariable(name="inquiryId") Long inquiryId){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}",principal);
        Member member = ((UserDetailsImpl)principal).getMember();
        log.info("member?:{}",member);

        inquiryService.inquiryDelete(inquiryId,member);

        return ResponseEntity.ok()
                .body("result : 문의 내용 삭제 완료");
    }
}