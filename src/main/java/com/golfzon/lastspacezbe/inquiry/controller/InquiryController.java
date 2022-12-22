package com.golfzon.lastspacezbe.inquiry.controller;


import com.golfzon.lastspacezbe.inquiry.dto.InquiryTotalResponseDto;
import com.golfzon.lastspacezbe.member.entity.Member;
import com.golfzon.lastspacezbe.inquiry.dto.InquiryRequestDto;
import com.golfzon.lastspacezbe.inquiry.service.InquiryService;
import com.golfzon.lastspacezbe.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {

    private final InquiryService inquiryService;

    // 문의하기
    @ApiOperation(value = "백오피스 문의내역", notes = "백오피스 문의내역 조회 기능입니다.")
    @PostMapping(value = "/post/{companyId}", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> inquiry(@PathVariable(name = "companyId") Long companyId,
                                          @RequestBody InquiryRequestDto requestDto) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}", principal);
        Member member = ((UserDetailsImpl) principal).getMember();
        log.info("member?:{}", member);

        inquiryService.inquiry(companyId, requestDto, member);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 문의내용 작성완료");
    }

    // 문의 내용 삭제
    @ApiOperation(value = "문의 내용 삭제", notes = "문의내용 삭제 기능입니다.")
    @DeleteMapping(value = "/delete/{inquiryId}", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> inquiryDelete(@PathVariable(name = "inquiryId") Long inquiryId) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}", principal);
        Member member = ((UserDetailsImpl) principal).getMember();
        log.info("member?:{}", member);

        inquiryService.inquiryDelete(inquiryId, member);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 문의 내용 삭제 완료");
    }

    // 답변 작성하기 / 수정하기
    @ApiOperation(value = "문의 답변 작성, 수정", notes = "문의내용 답변/수정 기능입니다.")
    @PutMapping(value = "/answer/{inquiryId}", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> answerPost(@PathVariable(name = "inquiryId") Long inquiryId,
                                             @RequestBody InquiryRequestDto requestDto) {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}", principal);
        Member member = ((UserDetailsImpl) principal).getMember();
        log.info("member?:{}", member);

        inquiryService.answerPost(inquiryId, requestDto, member);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 답변 작성완료");
    }

    // 답변 삭제
    @ApiOperation(value = "문의 답변 수정", notes = "문의내용 답변 삭제 기능입니다.")
    @PutMapping(value = "/answer/delete/{inquiryId}", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> answerDelete(@PathVariable(name = "inquiryId") Long inquiryId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("principal:{}", principal);
        Member member = ((UserDetailsImpl) principal).getMember();
        log.info("member?:{}", member);

        inquiryService.answerDelete(inquiryId, member);

        return ResponseEntity.ok()
                .contentType(new MediaType("application", "json", StandardCharsets.UTF_8))
                .body("result : 답변 삭제완료");
    }

    // 문의 내역 (업체 정보)
    @ApiOperation(value = "문의내역", notes = "문의내역 조회 기능입니다.")
    @GetMapping("/total/{companyId}")
    public ResponseEntity<Map<String,Object>> totalInquiry(@PathVariable(name = "companyId") Long companyId) {
        Map<String,Object> map = new HashMap<>();

        List<InquiryTotalResponseDto> totalResponseDtoList = inquiryService.totalInquiry(companyId);
        map.put("list",totalResponseDtoList);
        map.put("count",totalResponseDtoList.size());

        return ResponseEntity.ok()
                .body(map);

    }
}
